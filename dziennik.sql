create table teachers (
    id_teacher numeric(10) primary key,
    name character varying(100)
);

create table classes (
    id_class numeric(10) primary key,
    educator numeric(10) references teachers
);

--end term grade should be integer >= 1 and <= 6
create type partial_grade as enum ('1','1+','2-','2','2+','3-','3','3+','4-','4','4+','5-','5','5+','6-','6');




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
    value partial_grade,
    weight int check(weight >= 0),
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