create table teachers (
    teacher_id numeric(10) check (teacher_id >= 0) primary key,
    name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
    surname character varying(100) not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying check(email like '%_@_%.__%'),
    phone character varying(9) check(phone ~ '[0-9]{9}')
);

create table classes (
    class_id numeric(10) check (class_id >= 0) primary key,
    name char(2) unique,
    educator numeric(10) references teachers
);

create table students (
    student_id numeric(10) check (student_id >= 0) primary key,
    name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
    surname character varying(100)not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying check(email like '%_@_%.__%'),
    phone character varying(9) check(phone ~ '[0-9]{9}')
);

create table classes_students (
	class_id numeric(10) references classes,
    student_id numeric(10) references students,
	primary key(class_id, student_id)
);

create table legal_guardians (
    guardian_id numeric(10) check (guardian_id >= 0) primary key,
    name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
    surname character varying(100)not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying check(email like '%_@_%.__%'),
    phone character varying(9) check(phone ~ '[0-9]{9}')
);

create table guardians_students (
    guardian_id numeric(10) references legal_guardians,
    student_id numeric(10) references students,
    primary key(guardian_id, student_id)
);

create table subjects (
    subject_id numeric(10) check (subject_id >= 0) primary key,
    name character varying(100)
);

create table lessons (
    lesson_id numeric(10) check (lesson_id >= 0) primary key,
    class_id numeric(10) not null references classes,
    subject_id numeric(10) not null references subjects,
    topic character varying(500) not null
);

create table absences (
    lesson_id numeric(10) references lessons,
    student_id numeric(10) references students
);

create table teachers_classes_subjects (
    teacher_id numeric(10) not null,
    subject_id numeric(10) not null,
    class_id numeric(10) not null references classes,
    primary key (teacher_id, subject_id),
    unique(subject_id, class_id)
);


create type grade as enum ('1','1+','2-','2','2+','3-','3','3+','4-','4','4+','5-','5','5+','6-','6');

create or replace function grade_to_numeric(g grade) returns numeric(3,2) as
$$
declare
    value numeric(3,2);
begin
    value = 0;
    if g::text like '%+' then
        value = value + 0.25;
    end if;
    if g::text like '%-' then
        value = value - 0.25;
    end if;
    return (left(g::text,1)::numeric(3,2))+value;
end;
$$
language plpgsql;   
create cast (grade as numeric(3,2)) with function grade_to_numeric(grade) as implicit;


create table grades (
    value grade not null,
    weight int not null check(weight >= 0),
    student_id numeric(10) references students,
    subject_id numeric(10) references subjects,
    teacher_id numeric(10) references teachers
);

create table exams (
    teacher_id numeric(10) references teachers,
    subject_id numeric(10) references subjects,
    date date not null check (date>now()),
    description character varying(100)
);


create or replace function teacher_subject_check()
returns trigger as $teacher_subject_check$
declare
    res int;
begin
    select into res count(*) from teacher_classes_subjects where teacher_id = new.teacher_id and subject_id = new.subject_id;
    if res = 0 then
        raise exception 'error: given teacher does not teach this subject';
    end if;
    return new;
end;
$teacher_subject_check$ language plpgsql;

create trigger grades_teacher_subject_check before insert or update on grades
    for each row execute procedure teacher_subject_check();

create trigger exams_teacher_subject_check before insert or update on exams
    for each row execute procedure teacher_subject_check();


create or replace function teacher_insert_check()
returns trigger as $teacher_insert_check$
begin
    if(new.teacher_id is null) then
        new.teacher_id = (select case when
        (select min(t3.teacher_id) from teachers t3) > 1 then 1
        else coalesce(min(t.teacher_id) + 1, 1) end "id"
    from teachers t left join teachers t2
    on t.teacher_id = t2.teacher_id - 1
    where t2.teacher_id is null);
    end if;
    return new;
end;
$teacher_insert_check$
language plpgsql;

--if id not given returns first not used value
create trigger teacher_insert_check before insert or update on teachers
    for each row execute procedure teacher_insert_check();


create or replace view class_subjects as
    select class_id, c.name"class_name", subject_id, s.name"subject_name" 
    from teachers_classes_subjects join classes c using(class_id) join subjects s using(subject_id);

create or replace view students_in_classes as
    select class_id, name, count(*)"students" from classes_students join classes using(class_id)
    group by class_id, name order by 1;

create or replace view classes_avg as
    select class_id, c.name"class_name", subject_id, s.name"subject_name", round(sum(value * weight) / sum(weight), 2)"avg" from grades g
    join classes_students cs using(student_id)
    join teachers_classes_subjects tcs using(subject_id,class_id)
    join classes c using(class_id)
    join subjects s using(subject_id)
    group by class_id, c.name, subject_id, s.name
    order by class_id;

create or replace function remove_student()
returns trigger as $remove_student$
begin
    delete from absences a where a.student_id = old.student_id;
    delete from grades g where g.student_id = old.student_id;
    delete from guardians_students gs where gs.student_id = old.student_id;
    delete from classes_students cs where cs.student_id = old.student_id;
    return old;
end;
$remove_student$
language plpgsql;

create trigger remove_student before delete on students
    for each row execute procedure remove_student();

create or replace function remove_legal_guardian()
returns trigger as $remove_legal_guardian$
begin
    SET session_replication_role = replica;
    delete from guardians_students gs where gs.guardian_id = old.guardian_id;
    SET session_replication_role = default;
    return old;
end;
$remove_legal_guardian$
language plpgsql;

create trigger remove_legal_guardian before delete on legal_guardians
    for each row  execute procedure remove_legal_guardian();

create or replace function remove_guardian_student()
returns trigger as $remove_guardian_student$
begin
    if((select count(*) from guardians_students) < 1) then
    SET session_replication_role = replica;
    delete from legal_guardians lg where lg.guardian_id = old.guardian_id;
    SET session_replication_role = default;
    end if;
    return old;
end;
$remove_guardian_student$
language plpgsql;

create trigger remove_guardian_student after delete on guardians_students
    for each row execute procedure remove_guardian_student();
