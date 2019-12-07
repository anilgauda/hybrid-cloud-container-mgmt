
-- Name: logs; Type: TABLE; Schema: public; Owner: postgres
CREATE TABLE public.logs
(
    id bigint NOT NULL,
    details character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT logs_pkey PRIMARY KEY (id)
);