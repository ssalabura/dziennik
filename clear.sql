drop table if exists teachers cascade;
drop table if exists slots cascade;
drop table if exists groups cascade;
drop table if exists students cascade;
drop table if exists groups_students cascade;
drop table if exists legal_guardians cascade;
drop table if exists guardians_students cascade;
drop table if exists subjects cascade;
drop table if exists teacher_subjects cascade;
drop table if exists lessons cascade;
drop table if exists absences cascade;
drop table if exists teachers_groups_subjects cascade;

drop type if exists grade cascade;
drop function if exists grade_to_numeric(grade) cascade;
drop cast if exists (grade as numeric(3,2));

drop table if exists grades cascade;
drop table if exists exams cascade;

drop function if exists teacher_insert_check() cascade;

drop view if exists groups_subjects cascade;
drop view if exists students_in_groups cascade;
drop view if exists groups_avg cascade;

drop function if exists absences_students_check() cascade;
drop function if exists student_to_group_check() cascade;
drop function if exists group_subject_add_check() cascade;
drop function if exists student_to_group_check() cascade;
drop function if exists grades_students_check()  cascade;
drop function if exists slot_add_check()  cascade;
drop function if exists lesson_check() cascade;
drop function if exists pesel_check() cascade;

