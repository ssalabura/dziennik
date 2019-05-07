create table teachers (
    teacher_id numeric(10) primary key,
    name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
    surname character varying(100) not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying check(email like '%_@__%.__%'),
    phone numeric(9)
);

--class name format [1-8][a-z]
create table classes (
    class_id numeric(10) primary key,
    name char(2) unique,
    educator numeric(10) references teachers
);

create table students (
    student_id numeric(10) primary key,
    name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
    surname character varying(100)not null check(surname ~ '^[A-Z][a-z-]*$'),
    class numeric(10) references classes,
    email character varying check(email like '%_@__%.__%'),
    phone numeric(9)
);

create table legal_guardians (
  guardian_id numeric(10) primary key,
  student numeric(10) not null references students,
  name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
  surname character varying(100)not null check(surname ~ '^[A-Z][a-z-]*$'),
  email character varying check(email like '%_@__%.__%'),
  phone numeric(9)
);

create table subjects (
    subject_id numeric(10) primary key,
    name character varying(100)
);

--Subjects given teacher can teach
create table teacher_subjects (
    teacher_id numeric(10) references teachers,
    subject_id numeric(10) references subjects,
    unique (teacher_id,subject_id)
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
    return ((substring(g::text from 1 for 1)::numeric(3,2))+value);
end;
$$
language plpgsql;   
create cast (grade as numeric(3,2)) with function grade_to_numeric(grade) as implicit;




create table grades (
    value grade,
    weight int check(weight >= 0),
    student numeric(10) references students,
    subject numeric(10) references subjects,
    teacher numeric(10) references teachers
);

create table exams (
    teacher numeric(10) references teachers,
    subject numeric(10) references subjects,
    date date not null check (date>now()),
    description character varying(100)
);


create or replace function teacher_subject_check()
returns trigger as $teacher_subject_check$
declare
    res int;
begin
    select into res count(*) from teacher_subjects where teacher = new.teacher and subject = new.subject;
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
              (select min(t3.teacher_id) from teachers t3) > 1 then 1 else coalesce(min(t.teacher_id) + 1, 1) end "id"
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

create or replace function classes_insert_check()
returns trigger as $classes_insert_check$
declare
    res int;
    year char;
    grp char;
begin
    year = left(new.name, 1);
    grp = right(new.name, 1);
    if(year <= '8' and year >= '0'
        and grp >= 'a' and grp <= 'z') then
            if(grp = 'a') then return new; end if;
            select into res count(*) from classes where ascii(right(name, 1))=ascii(grp)-1;
            if(res > 0) then return new; end if;
        end if;
    raise exception 'error: incorrect class name';
end;
$classes_insert_check$
language plpgsql;

create trigger classes_insert_check before insert or update on classes
for each row execute procedure classes_insert_check();