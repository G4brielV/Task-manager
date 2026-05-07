ALTER TABLE tasks ADD COLUMN search_vector tsvector;

UPDATE tasks 
SET search_vector = to_tsvector('portuguese', coalesce(title, '') || ' ' || coalesce(description, ''));

CREATE INDEX idx_tasks_search_vector ON tasks USING GIN(search_vector);

CREATE FUNCTION tasks_search_vector_trigger() RETURNS trigger AS $$
begin
  new.search_vector :=
     to_tsvector('portuguese', coalesce(new.title, '') || ' ' || coalesce(new.description, ''));
  return new;
end
$$ LANGUAGE plpgsql;

CREATE TRIGGER tsvector_update BEFORE INSERT OR UPDATE
ON tasks FOR EACH ROW EXECUTE PROCEDURE tasks_search_vector_trigger();