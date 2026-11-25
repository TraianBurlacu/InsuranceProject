insert into countries (name) values ('Romania');

insert into counties (name, country_id)
values ('Cluj', 1), ('Bucuresti', 1);

insert into cities (name, county_id)
values ('Cluj-Napoca', 1), ('Turda', 1), ('Bucuresti', 2);
