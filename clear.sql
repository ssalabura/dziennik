drop table if exists teachers cascade;
drop table if exists classes cascade;
drop table if exists students cascade;
drop table if exists subjects cascade;
drop table if exists teacher_subjects cascade;
drop table if exists grades cascade;
drop table if exists exams cascade;

drop function if exists teacher_subject_check() cascade;

drop sequence if exists next_teacher_id;

drop function if exists teacher_insert_check() cascade;

drop function if exists classes_insert_check() cascade;

drop type if exists partial_grade;
