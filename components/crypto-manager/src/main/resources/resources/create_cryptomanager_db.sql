CREATE TABLE identifiers(
	identifier varchar(255) NOT NULL,
	PRIMARY KEY (identifier)
);

-- Erzeuge Tabelle fuer die Algorithmen-Namen
-- (id) dient nur als Index und wird automatisch generiert (aufsteigende Zahlen)
-- Beim "Insert" muss das Feld (id) entweder auf "DEFAULT" gesetzt oder freigelassen werden.
CREATE TABLE algorithms(
	algorithm_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL,
	PRIMARY KEY (algorithm_id),
	UNIQUE (name)
);

-- Erzeuge Tabelle fuer kryptographische Schluessel
-- Der Fremdschluessel auf die "Algorithms-Tabelle" bewirkt, dass beim Loeschen eines Algorithmus' 
-- automatisch alle dazugehoerigen Eintraege in der "keys"-Tabelle geloescht werden.
CREATE TABLE keys(
	alias VARCHAR(255) NOT NULL,
	algorithm_id INTEGER NOT NULL,
	identifier VARCHAR(255) NOT NULL UNIQUE,
	PRIMARY KEY (identifier, algorithm_id),
	FOREIGN KEY (algorithm_id) REFERENCES algorithms (algorithm_id) ON UPDATE RESTRICT ON DELETE CASCADE,
	FOREIGN KEY (identifier) REFERENCES identifiers (identifier) ON UPDATE RESTRICT ON DELETE CASCADE
);


-- Erzeuge einen "View", der nur die Identifier, die Algorithmennamen und die kryptographischen Schluessel
-- die zueinander gehoeren enthaelt.
-- Ein "View" ist eine Tabelle, die zur Laufzeit aus einer SELECT-Query erstellt wird. Sie kann nur zum Lesen
-- verwendet werden, jedoch nicht zum Schreiben. Zum Schreiben muss man die "richtigen" Tabellen verwenden.
CREATE VIEW key_assoc(identifier, algorithm_name, alias) AS
  (SELECT identifiers.identifier AS identifier,
	  algorithms.name AS algorithm_name,
	  keys.alias AS alias
   FROM identifiers, algorithms, keys
   WHERE keys.algorithm_id=algorithms.algorithm_id 
         AND keys.identifier=identifiers.identifier
);

-- ------------------------------------------------------
-- Beispiele
-- ------------------------------------------------------

-- Einen Identifier anlegen
insert into identifiers values ('hydrademo-rsa');

-- Einen Algorithmus anlegen
insert into algorithms values (DEFAULT, 'RSA');

-- Einen Schluessel fuer einen Algorithmus und Identifier anlegen
insert into keys values ('hydrademo-rsa', 1, 'hydrademo-rsa');

-- Die Tabelle "keys" ausgeben
--select * from keys;
select * from key_assoc;

-- Versuchen, die (id) eines Algorithmus zu aendern: Resultiert in einer Fehlermeldung, da
-- man automatisch generierte Felder nicht aendern darf
-- update algorithms set algorithm_id = 2;

-- Löschen eines Algorithmus: Hat zur Folge, dass auch alle Schluessel, die zum diesem Algorithmus gehören automatisch gelöscht werden.
--DELETE FROM algorithms WHERE id=1;

-- Die View ausgeben
--select * from key_assoc;

