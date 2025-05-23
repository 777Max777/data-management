CREATE SEQUENCE public.cases_id_seq
    INCREMENT 1
    START 4
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
CREATE SEQUENCE public.entities_id_seq
    INCREMENT 1
    START 62
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
CREATE SEQUENCE public.fields_id_seq
    INCREMENT 1
    START 19
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
CREATE SEQUENCE public.groups_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
CREATE SEQUENCE public.instances_id_seq
    INCREMENT 1
    START 19
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
CREATE SEQUENCE public.mappings_id_seq
    INCREMENT 1
    START 11
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
CREATE SEQUENCE public.tasks_id_seq
    INCREMENT 1
    START 18
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.tasks_id_seq
    OWNER TO postgres;
ALTER SEQUENCE public.mappings_id_seq
    OWNER TO postgres;
ALTER SEQUENCE public.instances_id_seq
    OWNER TO postgres;

ALTER SEQUENCE public.groups_id_seq
    OWNER TO postgres;
ALTER SEQUENCE public.fields_id_seq
    OWNER TO postgres;

ALTER SEQUENCE public.entities_id_seq
    OWNER TO postgres;

ALTER SEQUENCE public.cases_id_seq
    OWNER TO postgres;