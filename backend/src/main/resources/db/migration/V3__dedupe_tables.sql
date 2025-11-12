-- Dedupe group and link tables
CREATE TABLE IF NOT EXISTS dedupe_groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by UUID NULL REFERENCES users(id),
    note VARCHAR(1000)
);

CREATE INDEX IF NOT EXISTS idx_dedupe_groups_status ON dedupe_groups(status);
CREATE INDEX IF NOT EXISTS idx_dedupe_groups_entity_type ON dedupe_groups(entity_type);

CREATE TABLE IF NOT EXISTS dedupe_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL REFERENCES dedupe_groups(id) ON DELETE CASCADE,
    entity_id UUID NOT NULL,
    score DOUBLE PRECISION NULL,
    reason VARCHAR(1000),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_dedupe_links_group ON dedupe_links(group_id);
CREATE INDEX IF NOT EXISTS idx_dedupe_links_entity ON dedupe_links(entity_id);





