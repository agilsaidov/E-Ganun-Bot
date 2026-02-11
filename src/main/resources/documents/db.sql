--Law table
CREATE TABLE ganun.law(
	law_no VARCHAR(50) NOT NULL,
	code_type VARCHAR(50) NOT NULL,
	law_text TEXT NOT NULL,
	PRIMARY KEY(law_no, code_type)
);

CREATE INDEX idx_law_code ON ganun.law(code_type);
CREATE INDEX idx_law_no ON ganun.law(law_no);
CREATE INDEX idx_law_code_no ON ganun.law(code_type,law_no);

--Users table
CREATE TABLE ganun.users(
	telegram_id BIGINT PRIMARY KEY,
	username VARCHAR(255),
	first_name VARCHAR(255),
	last_name VARCHAR(255),
	is_bot BOOLEAN DEFAULT FALSE,
	is_active BOOLEAN DEFAULT TRUE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_created ON ganun.users(created_at);
CREATE INDEX idx_users_active ON ganun.users(is_active);

-- Bot usage table
CREATE TABLE ganun.usage(
	id SERIAL PRIMARY KEY,
	telegram_id BIGINT NOT NULL,
	total_searches INTEGER DEFAULT 0,
	last_search_date TIMESTAMP,
	last_search_query VARCHAR(255),
	last_search_code VARCHAR(255),
	total_messages INTEGER DEFAULT 0,
	first_usage_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY(telegram_id) REFERENCES ganun.users(telegram_id) ON DELETE CASCADE
);

CREATE INDEX idx_usage_telegram_id ON ganun.usage(telegram_id);
CREATE INDEX idx_usage_last_search ON ganun.usage(last_search_date);
CREATE UNIQUE INDEX idx_usage_telegram_unique ON ganun.usage(telegram_id);