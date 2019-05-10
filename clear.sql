drop table if exists teachers cascade;
drop table if exists classes cascade;
drop table if exists students cascade;
drop table if exists subjects cascade;
drop table if exists teacher_subjects cascade;
drop table if exists grades cascade;
drop table if exists exams cascade;
drop table if exists legal_guardians cascade;
drop table if exists guardians_students cascade;
drop table if exists lessons cascade;
drop table if exists absences cascade;

drop view if exists classes_avg cascade;
drop view if exists  students_in_classes cascade;


drop function if exists teacher_subject_check() cascade;
drop function if exists teacher_insert_check() cascade;
drop function if exists classes_insert_check() cascade;
drop function if exists remove_student() cascade;
drop function if exists remove_legal_guardian() cascade;
drop function if exists remove_guardian_student() cascade;

drop cast if exists (grade as numeric(3,2));
drop function if exists grade_to_numeric(grade);
drop type if exists grade;