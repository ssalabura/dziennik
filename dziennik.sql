create table teachers (
    teacher_id numeric(10) check (teacher_id >= 0) primary key,
    PESEL character varying(11) not null,
    name character varying(1024) not null check(name ~ '^([[:upper:]][[:lower:]]+\s?)+$'),
    surname character varying(1024) not null check(surname ~ '^([[:upper:]][[:lower:]]+-?)+$'),
    city character varying(128) not null,
    street character varying(128) not null,
    postalCode character varying(16) not null,
    email character varying(256) check(email ~ '[A-Z0-9._%-]+@[A-Z0-9._%-]+\.[A-Z]{2,4}'),
    phone character varying(16) check(phone ~ '[0-9]+')
);

create table groups (
    group_id numeric(10) check (group_id >= 0) primary key,
    name character varying(128) not null unique
);

create table students (
    student_id numeric(10) check (student_id >= 0) primary key,
    PESEL character varying(11) not null,
    name character varying(1024) not null check(name ~ '^([[:upper:]][[:lower:]]+\s?)+$'),
    surname character varying(1024) not null check(surname ~ '^([[:upper:]][[:lower:]]+-?)+$'),
    city character varying(128) not null,
    street character varying(128) not null,
    postalCode character varying(16) not null,
    email character varying(256) check(email ~ '[A-Z0-9._%-]+@[A-Z0-9._%-]+\.[A-Z]{2,4}'),
    phone character varying(16) check(phone ~ '[0-9]+')
);

create table groups_students (
    group_id numeric(10) references groups on delete cascade,
    student_id numeric(10) references students on delete cascade,
    primary key(group_id, student_id)
);

create table legal_guardians (
    guardian_id numeric(10) check (guardian_id >= 0) primary key,
    PESEL character varying(11) not null,
    name character varying(1024) not null check(name ~ '^([[:upper:]][[:lower:]]+\s?)+$'),
    surname character varying(1024) not null check(surname ~ '^([[:upper:]][[:lower:]]+-?)+$'),
    city character varying(128) not null,
    street character varying(128) not null,
    postalCode character varying(16) not null,
    email character varying(256) check(email ~ '[A-Z0-9._%-]+@[A-Z0-9._%-]+\.[A-Z]{2,4}'),
    phone character varying(16) check(phone ~ '[0-9]+')
);

create table guardians_students (
    guardian_id numeric(10) references legal_guardians on delete cascade,
    student_id numeric(10) references students on delete cascade,
    primary key(guardian_id, student_id)
);

create table subjects (
    subject_id numeric(10) check (subject_id >= 0) primary key,
    name character varying(128) not null
);

---Subjects given teacher can teach
create table teacher_subjects (
    teacher_id numeric(10) references teachers on delete cascade,
    subject_id numeric(10) references subjects on delete cascade,
    primary key (teacher_id,subject_id)
);

create table lessons (
    lesson_id numeric(10) check (lesson_id >= 0) primary key,
    group_id numeric(10) not null references groups on delete cascade,
    subject_id numeric(10) not null references subjects on delete cascade,
    topic character varying(256) not null,
    date date not null check(date > now())
);

create table absences (
    lesson_id numeric(10) references lessons on delete cascade,
    student_id numeric(10) references students on delete cascade,
    absence_type character check(absence_type ~ '[ONSZ]'),
    primary key(lesson_id, student_id)
);

create table teachers_groups_subjects (
    teacher_id numeric(10) not null,
    subject_id numeric(10) not null,
    group_id numeric(10) not null references groups on delete cascade,
    primary key (subject_id, group_id),
    foreign key (teacher_id, subject_id) references teacher_subjects on delete cascade
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
    student_id numeric(10) not null references students on delete cascade,
    subject_id numeric(10) not null,
    teacher_id numeric(10) not null,
    foreign key (teacher_id, subject_id) references teacher_subjects on delete cascade
);

create table exams (
    teacher_id numeric(10) not null,
    subject_id numeric(10) not null,
    group_id numeric(10) not null,
    date date not null check (date>now()),
    description character varying(256),
    foreign key (subject_id, group_id) references teachers_groups_subjects on delete cascade
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
    select group_id, name, student_id from groups_students join groups using(group_id) order by 1;

create or replace view groups_avg as
    select group_id, c.name"group_name", subject_id, s.name"subject_name", round(sum(value * weight) / sum(weight), 2)"avg" from grades g
    join groups_students cs using(student_id)
    join teachers_groups_subjects tcs using(subject_id,group_id)
    join groups c using(group_id)
    join subjects s using(subject_id)
    group by group_id, c.name, subject_id, s.name
    order by group_id;


create or replace function absences_students_check()
returns trigger as $absences_students_check$
declare
    am_i_in_given_group int;
    my_group numeric(10);
begin
    select into my_group group_id from lessons join groups using(group_id) where lesson_id = new.lesson_id;
    select into am_i_in_given_group count(*) from groups_students
        where group_id = my_group and student_id = new.student_id;
    if am_i_in_given_group = 0 then
        raise exception 'Error: Trying to set an absence of a student which is not in given group';
    end if;
    return new;

end;
$absences_students_check$
language plpgsql;

create trigger absences_students_check before insert or update on absences
    for each row execute procedure absences_students_check();

--BEGIN PESEL 

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

--END PESEL 
