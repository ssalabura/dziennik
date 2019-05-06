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

create table grades (
    grade numeric(1),
    id_student numeric(10) references students,
    subject numeric(10) references subjects,
    teacher numeric(10) references teachers
);

create table teacher_subjects (
    id_teacher numeric(10) references teachers,
    id_subjects numeric(10) references subjects
);

