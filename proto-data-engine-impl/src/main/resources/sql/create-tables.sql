create table entities
(
    id bigint not null
        constraint entities_pk
            primary key,
    name varchar(255),
    entity_parent_id bigint
        constraint entities_fk
            references entities,
    description varchar(255)
);

create table groups
(
    id bigint not null
        constraint group_pk
            primary key,
    name varchar(255)
);

create table fields
(
    id bigint not null
        constraint fields_pk
            primary key,
    name varchar(255),
    type character varying(15) COLLATE pg_catalog."default",
    rel_entity_id bigint
        constraint rel_entity_fk
            references entities,
    field_group_id bigint
        constraint fields_fk
            references groups
);

create table mappings
(
    entity_id bigint
        constraint mappings_entity_fk
            references entities,
    field_id bigint
        constraint mappings_field_fk
            references fields
);

create table instances
(
    id bigint not null
        constraint instances_pk
            primary key,
    name varchar(255),
    entity_id bigint
        constraint instance_to_entity_fk
            references entities,
    instance_parent_id bigint
        constraint instance_to_parent_fk
            references instances,
    data jsonb
);

create table relations
(
    instance_id bigint
        constraint relations_instance_fk1
            references instances,
    relation_id bigint
        constraint relations_instance_fk2
            references instances,
    field_id bigint
        constraint relations_fields_fk
            references fields
);


CREATE TABLE public.tasks
(
    id bigint NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    type character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    merge_data jsonb,
    is_synchronous boolean,
    init_data jsonb,
    task_data jsonb,
    CONSTRAINT tasks_pkey PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.tasks
    OWNER to lma;

CREATE TABLE public.cases
(
    id bigint NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT cases_pkey PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.cases
    OWNER to lma;

CREATE TABLE public.links
(
    task_id bigint NOT NULL,
    linked_id bigint NOT NULL,
    type_link character varying COLLATE pg_catalog."default",
    CONSTRAINT links_linked_id_fkey FOREIGN KEY (linked_id)
        REFERENCES public.tasks (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT links_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.links
    OWNER to lma;

CREATE TABLE public.map_case_task
(
    case_id bigint NOT NULL,
    task_id bigint NOT NULL,
    CONSTRAINT map_case_task_case_id_fkey FOREIGN KEY (case_id)
        REFERENCES public.cases (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT map_case_task_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.map_case_task
    OWNER to lma;

CREATE TABLE public.map_hierarchy_task
(
    task_id bigint NOT NULL,
    parent_task_id bigint NOT NULL,
    CONSTRAINT map_hierarchy_task_parent_task_id_fkey FOREIGN KEY (parent_task_id)
        REFERENCES public.tasks (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT map_hierarchy_task_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.map_hierarchy_task
    OWNER to lma;

create unique index group_id_uindex
    on groups (id);

create unique index entities_id_uindex
    on entities (id);

create unique index fields_id_uindex
    on fields (id);

create unique index instances_id_uindex
    on instances (id);

alter table relations owner to lma;
alter table instances owner to lma;
alter table mappings owner to lma;
alter table fields owner to lma;
alter table groups owner to lma;
alter table entities owner to lma;

create unique index if not exists entities_id_uindex
    on entities (id);
create unique index if not exists fields_id_uindex
    on fields (id);
create unique index if not exists group_id_uindex
    on groups (id);
create unique index if not exists instances_id_uindex
    on instances (id);