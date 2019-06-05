create table teachers (
    teacher_id numeric(10) check (teacher_id >= 0) primary key,
    PESEL character varying(11) not null,
    name character varying(1024) not null check(name ~ '^([[:upper:]][[:lower:]]+\s?)+$'),
    surname character varying(1024) not null check(surname ~ '^([[:upper:]][[:lower:]]+-?)+$'),
    city character varying(128) not null,
    street character varying(128) not null,
    postalCode character varying(16) not null,
    email character varying(256) check(email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\.[A-Za-z]{2,4}$'),
    phone character varying(16) check(phone ~ '^\+?[0-9]+$'),
    password character varying(100) not null
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
    email character varying(256) check(email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\.[A-Za-z]{2,4}$'),
    phone character varying(16) check(phone ~ '^\+?[0-9]+$'),
    password character varying(100) not null
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
    email character varying(256) check(email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\.[A-Za-z]{2,4}$'),
    phone character varying(16) check(phone ~ '^\+?[0-9]+$'),
    password character varying(100) not null
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

create table slots(
    slot numeric(10) primary key,
    start_time time not null
);

create table lessons (
    lesson_id numeric(10) check (lesson_id >= 0) primary key,
    group_id numeric(10) not null references groups on delete cascade,
    subject_id numeric(10) not null references subjects on delete cascade,
    topic character varying(256) not null,
    date date not null,
    slot numeric(10) not null references slots,
    unique (date,slot,group_id)
);

create table absences (
    lesson_id numeric(10) references lessons on delete cascade,
    student_id numeric(10) references students on delete cascade,
    primary key(lesson_id, student_id)
);


create table teachers_groups_subjects (
    teacher_id numeric(10) not null,
    subject_id numeric(10) not null,
    group_id numeric(10) not null references groups on delete cascade,
    day_id numeric(1) not null check(day_id >= 1 and day_id <= 7),
    slot numeric(10) not null references slots,
    primary key (subject_id, group_id, day_id,slot),
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
    grade_id serial primary key,
    foreign key (teacher_id, subject_id) references teacher_subjects on delete cascade
);

create table exams (
    exam_id serial primary key,
    lesson_id numeric(10) not null references lessons,
    description character varying(256));


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

create or replace view groups_subjects_plan as
    select group_id, c.name"group_name", subject_id, s.name"subject_name", day_id, slot
    from teachers_groups_subjects join groups c using(group_id) join subjects s using(subject_id) order by 1,2,3;

create or replace view students_in_groups as
    select group_id, name, student_id from groups_students join groups using(group_id) order by 1;

create or replace view students_subjects as
    select distinct sg.student_id, tgs.subject_id from students_in_groups sg
        join teachers_groups_subjects tgs on(tgs.group_id = sg.group_id ) order by 1,2;

create or replace view groups_avg as
    select group_id, c.name"group_name", subject_id, s.name"subject_name", round(sum(value * weight) / sum(weight), 2)"avg" from grades g
    join groups_students cs using(student_id)
    join teachers_groups_subjects tcs using(subject_id,group_id)
    join groups c using(group_id)
    join subjects s using(subject_id)
    group by group_id, c.name, subject_id, s.name
    order by group_id;


--TRIGGER FOR ADDING SLOT
create or replace function slot_add_check()
returns trigger as $slot_add_check$
declare
    ov int;
    tim time;
    n int;
begin
    select into ov count(*) from slots where (start_time,start_time + '45 minutes') overlaps (new.start_time, new.start_time + '45 minutes');
    select into n count(*) from slots where slot < new.slot;
    select into tim max(start_time) from slots where slot < new.slot;
    if ov > 0 then
        raise exception 'Error: lessons slots cannot overlap';
    end if;
    if n+1 < new.slot then
        raise exception 'Error: slots must be indexed with 1..n without gaps';
    end if;
    if tim > new.start_time then
        raise exception 'Error: lesson with higher slot must start later';
    end if;
    return new;
end;
$slot_add_check$
language plpgsql;

create trigger slot_add_check before insert or update on slots
    for each row execute procedure slot_add_check();


--TRIGGER ON ADDDING SUBJECTS TO GROUP
--GENERALLY ONE SHOULD AVOID ADDING SUBJECTS TO GROUPS WHEN GROUP HAS STUDENTS
create or replace function group_subject_add_check()
returns trigger as $group_subject_add_check$
declare
    students_conflicts int;
    teachers_conflicts int;
begin
    --get all students in this group, then get all groups where any students of new group belong 
    select into students_conflicts count(*) from groups_students gs1 join groups_students gs2 using(student_id) 
        join groups_subjects_plan p on(gs2.group_id = p.group_id)
        where gs1.group_id = new.group_id and p.slot = new.slot and p.day_id = new.day_id;
    
    select into teachers_conflicts count(*) from teachers_groups_subjects ts
        where ts.slot = new.slot and ts.day_id = new.day_id and ts.teacher_id = new.teacher_id;
    
    if teachers_conflicts > 0 then
        raise exception 'Error: after adding this subject to group there will be a teacher that has two or more lessons at the same time';
    end if;
    if students_conflicts > 0 then
        raise exception 'Error: after adding this subject to group there will be a student that has two or more lessons at the same time';
    end if;
    return new;
end;
$group_subject_add_check$
language plpgsql;

create trigger group_subject_add_check before insert or update on teachers_groups_subjects
    for each row execute procedure group_subject_add_check();


--TRIGGER ON ADDDING STUDENTS TO GROUP
create or replace function student_to_group_check()
returns trigger as $student_to_group_check$
declare
    conflicts int;
begin
    select into conflicts count(*) from groups_students gs join groups_subjects_plan p1 using(group_id) join groups_subjects_plan p2 using(day_id,slot)
        where gs.student_id = new.student_id and p2.group_id = new.group_id;
    if conflicts > 0 then
        raise exception 'Error: after adding to this group student will have two or more lessons at the same time';
    end if;
    return new;
end;
$student_to_group_check$
language plpgsql;

create trigger student_to_group_check before insert or update on groups_students
    for each row execute procedure student_to_group_check();

--GRADES INSERT TRIGGER
create or replace function grades_students_check()
returns trigger as $grades_students_check$
declare
    learns_subject int;
    teaches int;
begin
    select into learns_subject count(*) from students_subjects where student_id = new.student_id and subject_id = new.subject_id;
    select into teaches count(*) from teachers_groups_subjects where teacher_id = new.teacher_id and subject_id = new.subject_id;
    if learns_subject = 0  then
        raise exception 'Error: Trying to add grade to student which does not learn given subject';
    end if;
    if teaches = 0  then
        raise exception 'Error: Trying to add grade, but given teacher does not teach this subject';
    end if;
    return new;

end;
$grades_students_check$
language plpgsql;

create trigger grades_students_check before insert or update on grades
    for each row execute procedure grades_students_check();


--LESSON INSERT TRIGGER
--checks if there is lesson in timetable with given day and slot(isodow extracts day of week from date)
create or replace function lesson_check()
returns trigger as $lesson_check$
declare
    n int;
begin
    select into n count(*) from groups_subjects_plan where group_id = new.group_id and subject_id = new.subject_id and slot = new.slot and day_id = extract(isodow from new.date);
    if n = 0 then
        raise exception 'Error: according to lessons schedule, this lesson should not be in given day or slot';
    end if;
    return new;

end;
$lesson_check$
language plpgsql;

create trigger lesson_check before insert or update on lessons
    for each row execute procedure lesson_check();


--ABSENCES INSERT TRIGGER
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
