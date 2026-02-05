-- roles
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_roles_name UNIQUE (name)
) ENGINE=InnoDB;

-- user_accounts (1:1 z employee)
CREATE TABLE IF NOT EXISTS user_accounts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  enabled BIT NOT NULL DEFAULT b'1',
  PRIMARY KEY (id),
  CONSTRAINT uk_user_accounts_employee UNIQUE (employee_id),
  CONSTRAINT fk_user_accounts_employee
    FOREIGN KEY (employee_id) REFERENCES employees(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- join table user_roles
CREATE TABLE IF NOT EXISTS user_roles (
  user_account_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_account_id, role_id),
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_account_id) REFERENCES user_accounts(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_id) REFERENCES roles(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

