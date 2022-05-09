CREATE DATABASE cruisecompany;

USE cruisecompany;

CREATE TABLE boats (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30),
    capacity INT
);

CREATE TABLE crews (
	id INT PRIMARY KEY AUTO_INCREMENT,
    boat_id INT UNIQUE, #Одна команда для одного корабля
    
    CONSTRAINT crew_boat_fk FOREIGN KEY (boat_id) REFERENCES boats(id) ON DELETE CASCADE
);

CREATE TABLE cadres (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20),
    surname VARCHAR(20),
    position VARCHAR(15),
    characteristic VARCHAR(400),
    crew_id INT,
    
    CONSTRAINT crew_fk FOREIGN KEY (crew_id) REFERENCES crews(id) ON DELETE SET NULL
);

CREATE TABLE ports (
    id INT PRIMARY KEY AUTO_INCREMENT,
    country VARCHAR(20),
    city VARCHAR(20)
);

CREATE TABLE cruises (
	id INT PRIMARY KEY AUTO_INCREMENT,
	boat_id INT,
    start_date DATE,
    end_date DATE,
    cost INT,

	CONSTRAINT cruise_boat_fk FOREIGN KEY (boat_id) REFERENCES boats(id) ON DELETE CASCADE
);

CREATE TABLE cruises_ports (
    cruise_id INT,
    port_id INT,
    seq_number INT,
    
    PRIMARY KEY(cruise_id, port_id, seq_number),
    
    CONSTRAINT m2m_cruise_fk FOREIGN KEY (cruise_id) REFERENCES cruises (id) ON DELETE CASCADE,
    CONSTRAINT m2m_port_fk FOREIGN KEY (port_id) REFERENCES ports (id) ON DELETE CASCADE
);

CREATE TABLE users (
	id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(32) NOT NULL,
    phone_number VARCHAR(20)
);

CREATE TABLE users_roles (
    user_id INT NOT NULL,
    role VARCHAR(10) NOT NULL,
    
    PRIMARY KEY (user_id, role),
    
    CONSTRAINT m2m_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_images (
	user_id INT NOT NULL,
    uri VARCHAR(30) primary key,
    
    CONSTRAINT users_images_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE orders (
	id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    cruise_id INT NOT NULL,
    seats INT NOT NULL,
    book_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    confirmed BOOL DEFAULT 0,
    
    CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT order_cruise_fk FOREIGN KEY (cruise_id) REFERENCES cruises(id) ON DELETE CASCADE
);

#---------------------------------------------------------------------------------------Add base values

USE cruisecompany;

INSERT INTO users (username, password, phone_number) VALUES ('admin', '3e3e6b0e5c1c68644fc5ce3cf060211d', '+38 (484) 234-32-34'), ('user', '2e1ef01b619313b6452c5c348f55cb26', '+2 (343) 234-56-78');
INSERT INTO users_roles VALUES (1, "USER"), (1, 'ADMIN'), (2, 'USER');

INSERT INTO boats (name, capacity) VALUES ('Harmony of the Seas', 20), ('Allure & Oasis of the Seas', 25), ('Britanik', 100);

INSERT INTO ports (country, city) VALUES ('Ukr', 'Odesa'), ('Ukr', 'Sevastopol'), ('Ukr', 'Feodosia');

INSERT INTO cruises (boat_id, start_date, end_date, cost) VALUES (3, '2022-03-01', '2022-03-08', 30000);
INSERT INTO cruises_ports VALUES (1, 1, 1), (1, 2, 2), (1, 3, 3), (1, 2, 4), (1, 1, 5);