CREATE TABLE IF NOT EXISTS collaborators (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(120) NOT NULL,
	cpf VARCHAR(11) NOT NULL UNIQUE,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS breakfast_items (
	id BIGSERIAL PRIMARY KEY,
	collaborator_id BIGINT NOT NULL,
	item_name VARCHAR(120) NOT NULL,
	breakfast_date DATE NOT NULL,
	item_status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	
	
	CONSTRAINT fk_breakfast_items_collaborator
		FOREIGN KEY (collaborator_id) 
		REFERENCES collaborators(id) 
		ON DELETE CASCADE,
	
	CONSTRAINT uk_item_data
		UNIQUE (item_name, breakfast_date)
);