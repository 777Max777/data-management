CREATE TABLE public.entities
(
    id bigint NOT NULL DEFAULT nextval('entities_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    entity_parent_id bigint,
    description character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT entities_pk PRIMARY KEY (id),
    CONSTRAINT entities_fk FOREIGN KEY (entity_parent_id)
        REFERENCES public.entities (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.groups
(
    id bigint NOT NULL DEFAULT nextval('groups_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT group_pk PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.fields
(
    id bigint NOT NULL DEFAULT nextval('fields_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    field_group_id bigint,
    type character varying(15) COLLATE pg_catalog."default",
    rel_entity_id bigint,
    CONSTRAINT fields_pk PRIMARY KEY (id),
    CONSTRAINT fields_fk FOREIGN KEY (field_group_id)
        REFERENCES public.groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT rel_entity_fk FOREIGN KEY (rel_entity_id)
        REFERENCES public.entities (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.mappings
(
    id bigint NOT NULL DEFAULT nextval('mappings_id_seq'::regclass),
    entity_id bigint,
    field_id bigint,
    CONSTRAINT mappings_pk PRIMARY KEY (id),
    CONSTRAINT mappings_entity_fk FOREIGN KEY (entity_id)
        REFERENCES public.entities (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT mappings_field_fk FOREIGN KEY (field_id)
        REFERENCES public.fields (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.instances
(
    id bigint NOT NULL DEFAULT nextval('instances_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    entity_id bigint,
    instance_parent_id bigint,
    data jsonb,
    CONSTRAINT instances_pk PRIMARY KEY (id),
    CONSTRAINT instance_to_entity_fk FOREIGN KEY (entity_id)
        REFERENCES public.entities (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT instance_to_parent_fk FOREIGN KEY (instance_parent_id)
        REFERENCES public.instances (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.relations
(
    instance_id bigint,
    relation_id bigint,
    field_id bigint,
    CONSTRAINT relations_fields_fk FOREIGN KEY (field_id)
        REFERENCES public.fields (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT relations_instance_fk1 FOREIGN KEY (instance_id)
        REFERENCES public.instances (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT relations_instance_fk2 FOREIGN KEY (relation_id)
        REFERENCES public.instances (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.tasks
(
    id bigint NOT NULL DEFAULT nextval('tasks_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    type character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    merge_data jsonb,
    is_synchronous boolean,
    init_data jsonb,
    task_data jsonb,
    remove_data jsonb,
    CONSTRAINT tasks_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.cases
(
    id bigint NOT NULL DEFAULT nextval('cases_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT cases_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

CREATE TABLE public.links
(
    task_id bigint NOT NULL,
    linked_id bigint NOT NULL,
    type_link character varying COLLATE pg_catalog."default",
    CONSTRAINT links_linked_task_id_fkey FOREIGN KEY (linked_id)
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

CREATE UNIQUE INDEX entities_id_uindex
    ON public.entities USING btree
    (id)
    TABLESPACE pg_default;

CREATE UNIQUE INDEX fields_id_uindex
    ON public.fields USING btree
    (id)
    TABLESPACE pg_default;
CREATE UNIQUE INDEX group_id_uindex
    ON public.groups USING btree
    (id)
    TABLESPACE pg_default;
CREATE UNIQUE INDEX instances_id_uindex
    ON public.instances USING btree
    (id)
    TABLESPACE pg_default;
CREATE UNIQUE INDEX mappings_id_uindex
    ON public.mappings USING btree
    (id)
    TABLESPACE pg_default;


ALTER TABLE public.entities
    OWNER to postgres;

ALTER TABLE public.cases
    OWNER to postgres;

ALTER TABLE public.fields
    OWNER to postgres;
ALTER TABLE public.groups
    OWNER to postgres;
ALTER TABLE public.instances
    OWNER to postgres;
ALTER TABLE public.links
    OWNER to postgres;
ALTER TABLE public.map_case_task
    OWNER to postgres;
ALTER TABLE public.map_hierarchy_task
    OWNER to postgres;
ALTER TABLE public.mappings
    OWNER to postgres;
ALTER TABLE public.relations
    OWNER to postgres;
ALTER TABLE public.tasks
    OWNER to postgres;