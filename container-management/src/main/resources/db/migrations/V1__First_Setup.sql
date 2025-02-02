--
-- PostgreSQL database dump
--

--
-- Name: applications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.applications (
    id bigint NOT NULL,
    cpu_max integer,
    mem_max integer,
    name character varying(255),
    registry_image_url character varying(255),
    user_id bigint NOT NULL
);

--
-- Name: container_deployment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.container_deployments (
    id bigint NOT NULL,
    container_id character varying(255),
    deployed_on timestamp without time zone,
    application_id bigint NOT NULL,
    vm_id bigint NOT NULL
);

--
-- Name: provider; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.providers (
    id bigint NOT NULL,
    name character varying(255)
);

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    avatar character varying(255),
    created timestamp without time zone,
    email character varying(255),
    password character varying(255),
    username character varying(255)
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: vm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vms (
    id bigint NOT NULL,
    host character varying(255),
    key_file_name character varying(255),
    last_access timestamp without time zone,
    username character varying(255),
    provider_id bigint NOT NULL,
    user_id bigint NOT NULL
);


--
-- Name: application application_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT application_pkey PRIMARY KEY (id);


--
-- Name: container_deployment container_deployment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.container_deployments
    ADD CONSTRAINT container_deployment_pkey PRIMARY KEY (id);


--
-- Name: provider provider_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.providers
    ADD CONSTRAINT provider_pkey PRIMARY KEY (id);


--
-- Name: vm uk_h7twjdnrbp60v7wmr4qc62u31; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vms
    ADD CONSTRAINT uk_h7twjdnrbp60v7wmr4qc62u31 UNIQUE (provider_id);


--
-- Name: container_deployment uk_omck8b4anmbm1ag2ve6icppc8; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.container_deployments
    ADD CONSTRAINT uk_omck8b4anmbm1ag2ve6icppc8 UNIQUE (vm_id);


--
-- Name: vm uk_t0967kdh2nii2afhh6jop0e33; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vms
    ADD CONSTRAINT uk_t0967kdh2nii2afhh6jop0e33 UNIQUE (user_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: vm vm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vms
    ADD CONSTRAINT vm_pkey PRIMARY KEY (id);


--
-- Name: vm fk3sicbd4yo5kaift8550kwixad; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vms
    ADD CONSTRAINT fk3sicbd4yo5kaift8550kwixad FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: container_deployment fk40951pi7h7brmcrsnnrvqi4h9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.container_deployments
    ADD CONSTRAINT fk40951pi7h7brmcrsnnrvqi4h9 FOREIGN KEY (application_id) REFERENCES public.applications(id);


--
-- Name: container_deployment fk97xc90dyvfyyv2olao82l0fwt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.container_deployments
    ADD CONSTRAINT fk97xc90dyvfyyv2olao82l0fwt FOREIGN KEY (vm_id) REFERENCES public.vms(id);


--
-- Name: application fkawte0mbtubellxed1dvpoxhdj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT fkawte0mbtubellxed1dvpoxhdj FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: vm fkmweodtt9k5xdm1xlk23cavjn3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vms
    ADD CONSTRAINT fkmweodtt9k5xdm1xlk23cavjn3 FOREIGN KEY (provider_id) REFERENCES public.providers(id);


--
-- PostgreSQL database dump complete
--
