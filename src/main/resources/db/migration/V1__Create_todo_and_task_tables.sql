CREATE SEQUENCE tasks_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

CREATE SEQUENCE todos_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;


CREATE TABLE public.tasks
(
    id integer NOT NULL DEFAULT nextval('tasks_id_seq'::regclass),
    description character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    todo_id integer,
    CONSTRAINT tasks_pkey PRIMARY KEY (id),
    CONSTRAINT fkndwtm2u4rdwdvsox5x3ulo80p FOREIGN KEY (todo_id)
        REFERENCES public.todos (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

CREATE TABLE public.todos
(
    id integer NOT NULL DEFAULT nextval('todos_id_seq'::regclass),
    description character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT todos_pkey PRIMARY KEY (id)
)

ALTER TABLE public.tasks
    OWNER to postgres;);
ALTER TABLE public.todos
    OWNER to postgres;

TABLESPACE pg_default;

