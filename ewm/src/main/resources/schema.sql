DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS compilations_events;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS categories;


CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);


CREATE TABLE IF NOT EXISTS categories (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT pk_categories PRIMARY KEY (id),
  CONSTRAINT UQ_NAME_CATEGORY UNIQUE (name)
);


CREATE TABLE IF NOT EXISTS events (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,

  title VARCHAR(120) NOT NULL,
  description VARCHAR(7000) NOT NULL,
  annotation VARCHAR(2000) NOT NULL,

  state VARCHAR(32) NOT NULL,

  category_id BIGINT NOT NULL,
  initiator_id BIGINT NOT NULL,

  created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  publish_date TIMESTAMP WITHOUT TIME ZONE,

  lat FLOAT NOT NULL,
  lon FLOAT NOT NULL,

  paid BOOLEAN NOT NULL,
  request_moderation BOOLEAN NOT NULL,

  participant_limit INT NOT NULL,

  CONSTRAINT pk_events PRIMARY KEY (id),
  FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE,
  FOREIGN KEY(initiator_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,

  event_id BIGINT NOT NULL,
  requester_id BIGINT NOT NULL,

  created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,

  status VARCHAR(50) NOT NULL,

  CONSTRAINT pk_requests PRIMARY KEY (id),
  CONSTRAINT UQ_REQUEST UNIQUE(event_id, requester_id),
  FOREIGN KEY(event_id) REFERENCES events(id) ON DELETE CASCADE,
  FOREIGN KEY(requester_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS compilations (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  title VARCHAR(255) NOT NULL,
  pinned BOOLEAN NOT NULL,
  CONSTRAINT pk_compilations PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS compilations_events (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  compilation_id BIGINT NOT NULL,
  event_id BIGINT NOT NULL,
  FOREIGN KEY(compilation_id) REFERENCES compilations(id) ON DELETE CASCADE,
  FOREIGN KEY(event_id) REFERENCES events(id) ON DELETE CASCADE
);




