select * from groups_students gs 
	join groups_subjects_plan p1 using(group_id)
	join groups_students gs2 using(student_id)
	join groups_subjects_plan p2 on(gs2.group_id = p2.group_id) 
	where p1.subject_id != p2.subject_id and p1.day_id = p2.day_id and p1.slot = p2.slot
