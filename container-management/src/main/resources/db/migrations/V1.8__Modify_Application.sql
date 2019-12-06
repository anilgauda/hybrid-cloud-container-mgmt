ALTER TABLE ONLY public.applications
RENAME COLUMN cpu_max TO cpu;

ALTER TABLE ONLY public.applications
RENAME COLUMN mem_max TO memory;