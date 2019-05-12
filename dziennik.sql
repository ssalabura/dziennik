create table teachers (
    teacher_id numeric(10) check (teacher_id >= 0) primary key,
    PESEL character varying(11) not null unique,
    name character varying(1024) not null check(name ~ '^([A-Z][a-z]*\s?)+'),
    surname character varying(1024) not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying(256) unique check(email like '%_@_%.__%'),
    phone character varying(16) unique check(phone ~ '[0-9]{9}')
);

create table groups (
    group_id numeric(10) check (group_id >= 0) primary key,
    name character varying(128) not null unique
);

create table students (
    student_id numeric(10) check (student_id >= 0) primary key,
    PESEL character varying(11) not null unique,
    name character varying(1024) not null check(name ~ '^([A-Z][a-z]*\s?)+'),
    surname character varying(1024) not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying(256) unique check(email like '%_@_%.__%'),
    phone character varying(16) unique check(phone ~ '[0-9]+')
);

create table groups_students (
    group_id numeric(10) references groups,
    student_id numeric(10) references students,
    primary key(group_id, student_id)
);

create table legal_guardians (
    guardian_id numeric(10) check (guardian_id >= 0) primary key,
    PESEL character varying(11) not null unique,
    name character varying(1024) not null check(name ~ '^([A-Z][a-z]*\s?)+'),
    surname character varying(1024) not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying(256) unique check(email like '%_@_%.__%'),
    phone character varying(16) unique check(phone ~ '[0-9]+')
);

create table guardians_students (
    guardian_id numeric(10) references legal_guardians,
    student_id numeric(10) references students,
    primary key(guardian_id, student_id)
);

create table subjects (
    subject_id numeric(10) check (subject_id >= 0) primary key,
    name character varying(128) not null
);

---Subjects given teacher can teach
create table teacher_subjects (
    teacher_id numeric(10) references teachers,
    subject_id numeric(10) references subjects,
    primary key (teacher_id,subject_id)
);

create table lessons (
    lesson_id numeric(10) check (lesson_id >= 0) primary key,
    group_id numeric(10) not null references groups,
    subject_id numeric(10) not null references subjects,
    topic character varying(256) not null
);

create table absences (
    lesson_id numeric(10) references lessons,
    student_id numeric(10) references students,
    primary key(lesson_id, student_id)
);

create table teachers_groups_subjects (
    teacher_id numeric(10) not null,
    subject_id numeric(10) not null,
    group_id numeric(10) not null references groups,
    primary key (subject_id, group_id),
    foreign key (teacher_id, subject_id) references teacher_subjects
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
    student_id numeric(10) not null references students,
    subject_id numeric(10) not null,
    teacher_id numeric(10) not null,
    foreign key (teacher_id, subject_id) references teacher_subjects
);

create table exams (
    teacher_id numeric(10) not null,
    subject_id numeric(10) not null,
    date date not null check (date>now()),
    description character varying(256),
    foreign key (teacher_id, subject_id) references teacher_subjects
);

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

create or replace view groups_subjects as
    select group_id, c.name"group_name", subject_id, s.name"subject_name" 
    from teachers_groups_subjects join groups c using(group_id) join subjects s using(subject_id);

create or replace view students_in_groups as
    select group_id, name, count(*)"students" from groups_students join groups using(group_id)
    group by group_id, name order by 1;

create or replace view groups_avg as
    select group_id, c.name"group_name", subject_id, s.name"subject_name", round(sum(value * weight) / sum(weight), 2)"avg" from grades g
    join groups_students cs using(student_id)
    join teachers_groups_subjects tcs using(subject_id,group_id)
    join groups c using(group_id)
    join subjects s using(subject_id)
    group by group_id, c.name, subject_id, s.name
    order by group_id;

create or replace function remove_student()
returns trigger as $remove_student$
begin
    delete from absences a where a.student_id = old.student_id;
    delete from grades g where g.student_id = old.student_id;
    delete from guardians_students gs where gs.student_id = old.student_id;
    delete from groups_students cs where cs.student_id = old.student_id;
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

CREATE OR REPLACE FUNCTION PESEL_check() RETURNS trigger AS $PESEL_check$
declare
sum numeric = 0;
BEGIN
if(length(new.PESEL) != 11) then
raise exception 'Error: Incorrect PESEL';
end if;
if(cast(substring(new.PESEL::text, 5, 2) as numeric) > 31 or cast(substring(new.PESEL::text, 5, 2) as numeric) < 1) then
raise exception 'Error: Incorrect PESEL';
end if;

sum = cast(substring(new.PESEL::text, 1, 1) as numeric) * 9 +
       cast(substring(new.PESEL::text, 2, 1) as numeric) * 7 +
       cast(substring(new.PESEL::text, 3, 1) as numeric) * 3 +
       cast(substring(new.PESEL::text, 4, 1) as numeric) * 1 +
       cast(substring(new.PESEL::text, 5, 1) as numeric) * 9 +
       cast(substring(new.PESEL::text, 6, 1) as numeric) * 7 +
       cast(substring(new.PESEL::text, 7, 1) as numeric) * 3 +
       cast(substring(new.PESEL::text, 8, 1) as numeric) * 1 +
       cast(substring(new.PESEL::text, 9, 1) as numeric) * 9 +
       cast(substring(new.PESEL::text, 10, 1) as numeric) * 7;
sum = sum % 10;
if(sum != cast(substring(new.PESEL::text, 11, 1) as numeric)) then
raise exception 'Error: Incorrect PESEL';
end if;
return new;
END;
$PESEL_check$ LANGUAGE plpgsql;

create trigger students_pesel_check before insert or update on students
    for each row  execute procedure PESEL_check();

create trigger guardians_pesel_check before insert or update on legal_guardians
    for each row  execute procedure PESEL_check();

create trigger teachers_pesel_check before insert or update on teachers
    for each row  execute procedure PESEL_check();