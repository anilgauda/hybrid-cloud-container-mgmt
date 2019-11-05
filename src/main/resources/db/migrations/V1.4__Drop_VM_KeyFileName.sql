ALTER TABLE public.vms
DROP COLUMN key_file_name;

ALTER TABLE public.vms
ADD COLUMN name character varying(255);