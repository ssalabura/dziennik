create table teachers (
    id_teacher numeric(10) primary key
);

create table classes (
    id_class numeric(10) primary key,
    educator numeric(10) references teachers
);

create table students (
    id_student numeric(10) primary key,
    name character varying(100),
    class numeric(10) references classes
);

create table subjects (
    id_subject numeric(10) primary key,
    name character varying(100)
);

--Subjects given teacher can teach
create table teacher_subjects (
    id_teacher numeric(10) references teachers,
    id_subjects numeric(10) references subjects
);

create table grades (
    grade numeric(1) check(grade <= 6),
    id_student numeric(10) references students,
    subject numeric(10) references subjects,
    teacher numeric(10) references teachers
);



create or replace function grades_teacher_check()
returns trigger as $grades_teacher_check$
declare
    res int;
begin
    select into res count(*) from teacher_subjects where id_teacher = new.teacher and id_subjects = new.subject;
    if res = 0 then
        raise exception 'cannot add new grade, given teacher does not teach this subject';
    end if;
    return new;
end;
$grades_teacher_check$ language plpgsql;
create trigger grades_teacher_check before insert or update on grades
for each row execute procedure grades_teacher_check();