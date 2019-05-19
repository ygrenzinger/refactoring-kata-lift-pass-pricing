CREATE TABLE base_price (
    pass_id INT AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL,
    cost INT NOT NULL,
    PRIMARY KEY (pass_id),
    UNIQUE (type)
);
INSERT INTO base_price (type, cost) VALUES ('1jour', 35);
INSERT INTO base_price (type, cost) VALUES ('night', 19);

CREATE TABLE holidays (
    holiday DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (holiday)
);
INSERT INTO holidays (holiday, description) VALUES ('2019-02-18', 'winter');
INSERT INTO holidays (holiday, description) VALUES ('2019-02-25', 'winter');
INSERT INTO holidays (holiday, description) VALUES ('2019-03-04', 'winter');
