ALTER TABLE ONLY public.vms DROP COLUMN memory;

ALTER TABLE public.vms
ALTER COLUMN private_key TYPE bytea USING private_key::TEXT::BYTEA