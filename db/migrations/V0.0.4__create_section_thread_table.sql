CREATE TABLE sections_threads (
  id UUID PRIMARY KEY DEFAULT uuidv7(),
  plan_id UUID REFERENCES plans,
  section_id UUID REFERENCES sections,
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);