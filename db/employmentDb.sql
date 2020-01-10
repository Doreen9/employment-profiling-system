CREATE TABLE employee (

	employee_id serial primary key,
	employee_firstName text not null,
	employee_lastName text not null,
	employee_phoneNumber text not null,
	employee_email text not null,
	employee_address text not null,
	employee_dev_level text not null,
	employee_hire_date DATE not null,
	employee_onLeave BOOLEAN not null,
	employee_gender text not null,
	employee_status text not null
);

CREATE TABLE project (

	project_id serial primary key,
	project_name text not null,
	project_description text not null,
	project_start_date DATE not null,
	project_end_date DATE not null,
	project_status text not null
);

CREATE TABLE tech (

	tech_id serial primary key,
	tech_name text not null,
	tech_status text not null
);

CREATE TABLE assignedproject (

	assignedproject_id serial primary key,
	employee_id INTEGER,
	project_id INTEGER
);

CREATE TABLE techproject (

	techproject_id serial primary key,
	tech_id INTEGER,
	project_id INTEGER
);

CREATE TABLE employeetech (
	techproject_id serial primary key,
	tech_id INTEGER,
	employee_id INTEGER
);

