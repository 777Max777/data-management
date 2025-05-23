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