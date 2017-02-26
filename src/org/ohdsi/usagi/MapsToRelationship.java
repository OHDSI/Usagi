package org.ohdsi.usagi;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

import org.ohdsi.utilities.files.Row;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class MapsToRelationship {
	@PrimaryKey
	public int	conceptId1;
	@SecondaryKey(relate=MANY_TO_ONE)
	public int	conceptId2;

	public MapsToRelationship() {}
	
	public MapsToRelationship(Row row) {
		conceptId1 = row.getInt("concept_id_1");
		conceptId2 = row.getInt("concept_id_2");
	}
}
