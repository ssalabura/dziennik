create table teachers (
    teacher_id numeric(10) check (teacher_id >= 0) primary key,
    name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
    surname character varying(100) not null check(surname ~ '^[A-Z][a-z-]*$'),
    email character varying check(email like '%_@_%.__%'),
    phone character varying(9) check(phone ~ '[0-9]{9}')
);

create table classes (
    class_id numeric(10) check (class_id >= 0) primary key,
    name char(2) check(name ~ '[1-8][a-z]') unique,
    educator numeric(10) references teachers
);

create table students (
    student_id numeric(10) check (student_id >= 0)  primary key,
    name character varying(100) not null check(name ~ '^[A-Z][a-z]*$'),
    surname character varying(100)not null check(surname ~ '^[A-Z][a-z-]*$'),
    class_id numeric(10) references classes,
    email character varying check(email like '%_@_%.__%'),
    phone character varying(9) check(phone ~ '[0-9]{9}')
);

create table legal_guardians (
  guardian_id numeric(10) check (guardian_id >= 0)  primary key,
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
    subject_id numeric(10) check (subject_id >= 0)  primary key,
    name character varying(100)
);

--Subjects given teacher can teach
create table teacher_subjects (
    teacher_id numeric(10) references teachers,
    subject_id numeric(10) references subjects,
    unique (teacher_id,subject_id)
);


create table lessons (
    lesson_id numeric(10) check (lesson_id >= 0)  primary key,
    class_id numeric(10) not null references classes,
    subject_id numeric(10) not null references subjects,
    topic character varying(500) not null
);

create table absences (
    lesson_id numeric(10) references lessons,
    student_id numeric(10) references students
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
    select into res count(*) from teacher_subjects where teacher_id = new.teacher_id and subject_id = new.subject_id;
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
    if(grp = 'a') then return new; end if;
    select into res count(*) from classes where ascii(right(name, 1))=ascii(grp)-1;
    if(res > 0) then return new; end if;
    raise exception 'error: incorrect class name';
end;
$classes_insert_check$
language plpgsql;

create trigger classes_insert_check before insert or update on classes
for each row execute procedure classes_insert_check();

create or replace view students_in_classes as
select c.class_id, c.name, count(*)"students" from classes c join students s on c.class_id = s.class_id
group by c.class_id order by 1;

create or replace view classes_avg as
select g.subject_id, c.class_id, c.name, round(avg(g.value), 2)"avg" from grades g join students s on g.student_id = s.student_id
join classes c on s.class_id = c.class_id
group by g.subject_id, c.class_id;

create or replace function remove_student()
returns trigger as $remove_student$
  declare
    r record;
  begin
    delete from absences a
        where a.student_id = old.student_id;

    delete from grades g
      where g.student_id = old.student_id;

    for r in select * from guardians_students gs
    where gs.student_id = old.student_id
    loop
      delete from guardians_students gs2
      where gs2.student_id = r.student_id and gs2.guardian_id = r.guardian_id;
      if((select count(*) from guardians_students gs3
        where gs3.guardian_id = r.guardian_id) < 1) then
          delete from legal_guardians lg
        where lg.guardian_id = r.guardian_id;
      end if;
    end loop;
    return old;
  end;
  $remove_student$
language plpgsql;

create trigger remove_student before delete on students
  for each row execute procedure remove_student();