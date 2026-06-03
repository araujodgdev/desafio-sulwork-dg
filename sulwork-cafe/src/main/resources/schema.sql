CREATE TABLE IF NOT EXISTS collaborators (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(120) NOT NULL,
	cpf VARCHAR(11) NOT NULL UNIQUE,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS breakfasts (
	id BIGSERIAL PRIMARY KEY,
	breakfast_date DATE NOT NULL UNIQUE,
	breakfast_time TIME,
	location VARCHAR(120),
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	
);


CREATE TABLE IF NOT EXISTS participations (
	id BIGSERIAL PRIMARY KEY,
	collaborator_id BIGINT NOT NULL,
	breakfast_id BIGINT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	
	
	CONSTRAINT fk_participations_breakfast
		FOREIGN KEY (breakfast_id)
		REFERENCES breakfasts(id)
		ON DELETE CASCADE,
		
	CONSTRAINT fk_participations_collaborator
		FOREIGN KEY (collaborator_id)
		REFERENCES collaborators(id)
		ON DELETE CASCADE,
		
	CONSTRAINT uk_participation_breakfast_collaborator
		UNIQUE (breakfast_id, collaborator_id)
);

CREATE TABLE IF NOT EXISTS breakfast_items (
	id BIGSERIAL PRIMARY KEY,
	breakfast_id BIGINT NOT NULL,
	participation_id BIGINT NOT NULL,
	item_name VARCHAR(120) NOT NULL,
	item_status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	
	
	CONSTRAINT fk_breakfast_items_breakfast
		FOREIGN KEY (breakfast_id) 
		REFERENCES breakfasts(id) 
		ON DELETE CASCADE,
	
	
	CONSTRAINT fk_breakfast_items_participation
		FOREIGN KEY (participation_id)
		REFERENCES participations(id)
		ON DELETE CASCADE,
	
	CONSTRAINT ck_breakfast_items_status
		CHECK (item_status IN ('PENDENTE', 'TROUXE', 'NAO_TROUXE'))

);

DROP INDEX IF EXISTS uk_breakfast_items_breakfast_name;