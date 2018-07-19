INSERT INTO projects (id, title) VALUES
	(1, 'PROJECT Team 3'),
	(2, 'PROJECT 2'),
	(3, 'PROJECT 3'),
	(4, 'PROJECT 4');

INSERT INTO users (id, email, first_name, last_name, is_admin) VALUES 
	(1, 'hgao3@bu.edu', 'Hugh', 'Gao', 1),
	(2, 'mchen15@bu.edu', 'Miffy', 'Chen', 0),
	(3, 'mhachey@bu.edu', 'Michael', 'Hachey', 0),
	(4, 'nehap86@bu.edu', 'Neha', 'Pawar', 0),
	(5, 'japarker@bu.edu', 'Jefferson', 'Parker', 0);
	
INSERT INTO users_projects (role, project_id, user_id) VALUES
	('Manager', 1, 1),
	('Manager', 2, 1),
	('Manager', 3, 1),
	('Manager', 4, 1),
	('Teammate', 1, 2),
	('Teammate', 1, 3),
	('Teammate', 1, 4),
	('Client', 1, 5);

INSERT INTO stages (id, title, project_id) VALUES
	(1, 'Backlog', 1),
	(2, 'To Do', 1),
	(3, 'In Progress', 1),
	(4, 'QA', 1),
	(5, 'Done', 1);

INSERT INTO tasks (id, created_at, updated_at, content, due_at, title, assigned_user_id, stage_id) VALUES
(1, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Set up backend and frontend of project.', '2018-06-15 09:00:00', 'Set up Project', 1, 1),
(2, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Set up database server of project.', '2018-06-15 09:00:00', 'Set up Database server', 1, 1),
(3, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Create database Schema sql', '2018-06-15 09:00:00', 'Create database Schema', 1, 1),
(4, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Draft security model (user roles and permissions)', '2018-06-15 09:00:00', 'Draft security model', 1, 1),
(5, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Write Wireframes / page sketches', '2018-06-15 09:00:00', 'Wireframes / page sketches', 1, 1),
(6, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'set up Test Plan', '2018-06-15 09:00:00', 'Test Plan', 1, 1),
(7, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Write Hibernate for user', '2018-06-15 09:00:00', 'ORM for User', 1, 1),
(8, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Write Hibernate for task', '2018-06-15 09:00:00', 'ORM for Task', 1, 1),
(9, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Write Hibernate for stage', '2018-06-15 09:00:00', 'ORM for Stage', 1, 1),
(10, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'Write Hibernate for project', '2018-06-15 09:00:00', 'ORM for Project', 1, 1);	
	
	
INSERT INTO issues (id, created_at, updated_at, content, open_status, priority_level, title, owner_user_id, task_id, project_id) VALUES 
(1, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'test do not work', 'true', 'High', 'bug1', 1, null, 1),
(2, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'test do not work', 'true', 'High', 'bug2', 1, null, 1);
(3, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'test do not work', 'true', 'High', 'bug3', 1, null),
(4, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'test do not work', 'true', 'High', 'bug4', 1, null),
(5, '2018-06-13 09:18:56', '2018-06-13 09:18:56', 'test do not work', 'true', 'High', 'bug5', 1, null);


	
	
	
	

