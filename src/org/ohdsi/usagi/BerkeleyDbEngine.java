package org.ohdsi.usagi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ohdsi.utilities.DirectoryUtilities;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;

public class BerkeleyDbEngine {
	public static String						DATABASE_FOLDER		= "sleepyCat";
	private Environment							dbEnvironment;
	private EntityStore							store;
	private ConceptDataAccessor					conceptDataAccessor;
	private MapsToRelationshipDataAccessor		mapsToRelationshipDataAccessor;
	private SubsumesRelationshipDataAccessor	subsumesRelationshipDataAccessor;
	private String								databaseFolder;
	private boolean								isOpenForReading	= false;
	private boolean								isOpenForWriting	= false;

	public BerkeleyDbEngine(String folder) {
		this.databaseFolder = folder + "/" + DATABASE_FOLDER;
	}

	public void createDatabase() {
		File folder = new File(databaseFolder);
		if (folder.exists())
			DirectoryUtilities.deleteDir(folder);
		new File(databaseFolder).mkdir();
		open(true);
		isOpenForWriting = true;
	}

	public void openForReading() {
		if (!isOpenForReading) {
			open(false);
			isOpenForReading = true;
		}
	}

	private void open(boolean create) {
		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(create);
			envConfig.setReadOnly(!create);
			dbEnvironment = new Environment(new File(databaseFolder), envConfig);

			StoreConfig storeConfig = new StoreConfig();
			storeConfig.setAllowCreate(create);
			storeConfig.setReadOnly(!create);
			store = new EntityStore(dbEnvironment, "EntityStore", storeConfig);
			conceptDataAccessor = new ConceptDataAccessor();
			mapsToRelationshipDataAccessor = new MapsToRelationshipDataAccessor();
			subsumesRelationshipDataAccessor = new SubsumesRelationshipDataAccessor();
		} catch (DatabaseException dbe) {
			throw new RuntimeException(dbe);
		}
	}

	public void put(Concept concept) {
		conceptDataAccessor.primaryIndex.put(concept);
	}

	public void put(MapsToRelationship mapsToRelationship) {
		mapsToRelationshipDataAccessor.primaryIndex.put(mapsToRelationship);
	}

	public void put(SubsumesRelationship subsumesRelationship) {
		subsumesRelationshipDataAccessor.primaryIndex.put(subsumesRelationship);
	}

	public EntityCursor<Concept> getConceptCursor() {
		return conceptDataAccessor.primaryIndex.entities();
	}

	public MapsToRelationship getMapsToRelationship(int conceptId) {
		return mapsToRelationshipDataAccessor.primaryIndex.get(conceptId);
	}

	public List<MapsToRelationship> getMapsToRelationshipsByConceptId2(int conceptId) {
		EntityIndex<Integer, MapsToRelationship> subIndex = mapsToRelationshipDataAccessor.secondaryIndex.subIndex(conceptId);
		EntityCursor<MapsToRelationship> cursor = subIndex.entities();
		List<MapsToRelationship> relationships = new ArrayList<MapsToRelationship>();
		try {
			for (MapsToRelationship relationship : cursor)
				relationships.add(relationship);
		} finally {
			cursor.close();
		}
		return relationships;
	}

	public List<SubsumesRelationship> getSubsumesRelationshipsByParentConceptId(int conceptId) {
		EntityIndex<Integer, SubsumesRelationship> subIndex = subsumesRelationshipDataAccessor.secondaryIndexParent.subIndex(conceptId);
		EntityCursor<SubsumesRelationship> cursor = subIndex.entities();
		List<SubsumesRelationship> relationships = new ArrayList<SubsumesRelationship>();
		try {
			for (SubsumesRelationship relationship : cursor)
				relationships.add(relationship);
		} finally {
			cursor.close();
		}
		return relationships;
	}
	
	public List<SubsumesRelationship> getSubsumesRelationshipsByChildConceptId(int conceptId) {
		EntityIndex<Integer, SubsumesRelationship> subIndex = subsumesRelationshipDataAccessor.secondaryIndexChild.subIndex(conceptId);
		EntityCursor<SubsumesRelationship> cursor = subIndex.entities();
		List<SubsumesRelationship> relationships = new ArrayList<SubsumesRelationship>();
		try {
			for (SubsumesRelationship relationship : cursor)
				relationships.add(relationship);
		} finally {
			cursor.close();
		}
		return relationships;
	}

	public Concept getConcept(int conceptId) {
		return conceptDataAccessor.primaryIndex.get(conceptId);
	}

	public void shutdown() throws DatabaseException {
		try {
			if (isOpenForReading || isOpenForWriting) {
				store.close();
				dbEnvironment.close();
			}
		} catch (DatabaseException dbe) {
			throw new RuntimeException(dbe);
		}
	}

	private class ConceptDataAccessor {
		public PrimaryIndex<Integer, Concept>	primaryIndex;

		public ConceptDataAccessor() throws DatabaseException {
			primaryIndex = store.getPrimaryIndex(Integer.class, Concept.class);
		}

	}

	private class MapsToRelationshipDataAccessor {
		public PrimaryIndex<Integer, MapsToRelationship>			primaryIndex;
		public SecondaryIndex<Integer, Integer, MapsToRelationship>	secondaryIndex;

		public MapsToRelationshipDataAccessor() throws DatabaseException {
			primaryIndex = store.getPrimaryIndex(Integer.class, MapsToRelationship.class);
			secondaryIndex = store.getSecondaryIndex(primaryIndex, Integer.class, "conceptId2");
		}
	}

	private class SubsumesRelationshipDataAccessor {
		public PrimaryIndex<Integer, SubsumesRelationship>				primaryIndex;
		public SecondaryIndex<Integer, Integer, SubsumesRelationship>	secondaryIndexParent;
		public SecondaryIndex<Integer, Integer, SubsumesRelationship>	secondaryIndexChild;

		public SubsumesRelationshipDataAccessor() throws DatabaseException {
			primaryIndex = store.getPrimaryIndex(Integer.class, SubsumesRelationship.class);
			secondaryIndexParent = store.getSecondaryIndex(primaryIndex, Integer.class, "parentConceptId");
			secondaryIndexChild = store.getSecondaryIndex(primaryIndex, Integer.class, "childConceptId");
		}
	}
}
