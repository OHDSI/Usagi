/*******************************************************************************
 * Copyright 2018 Observational Health Data Sciences and Informatics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohdsi.utilities.collections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class IntHashSet {

	public static void main(String[] args) {
		Random random = new Random();
//		IntHashSet set = new IntHashSet();
		HashSet<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < 1000000; i++) {
			int x = random.nextInt();
			set.add(x);
		}
		System.gc();
		long heapSize = Runtime.getRuntime().totalMemory();
//		long heapMaxSize = Runtime.getRuntime().maxMemory();
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		System.out.println(heapSize - heapFreeSize);
	}

	private static int	defaultCapacity	= 16;
	private int[]		baseHash;
	private int			capacity;
	private int			firstEmpty;
	private int			hashFactor;
	private int[]		keys;
	private int[]		next;
	private int			prev;
	private int			size;

	private final class IndexIterator {
		private int	baseHashIndex	= 0;
		private int	index			= 0;
		private int	lastIndex		= 0;

		public IndexIterator() {
			for (baseHashIndex = 0; baseHashIndex < baseHash.length; ++baseHashIndex) {
				index = baseHash[baseHashIndex];
				if (index != 0) {
					break;
				}
			}
		}

		public boolean hasNext() {
			return (index != 0);
		}

		public int next() {
			lastIndex = index;
			index = next[index];
			while (index == 0 && ++baseHashIndex < baseHash.length) {
				index = baseHash[baseHashIndex];
			}

			return lastIndex;
		}

	}

	public IntHashSet() {
		this(defaultCapacity);
	}

	public IntHashSet(int capacity) {
		this.capacity = 16;
		while (this.capacity < capacity) {
			this.capacity <<= 1;
		}
		int arrayLength = this.capacity + 1;
		this.keys = new int[arrayLength];
		this.next = new int[arrayLength];
		int baseHashSize = this.capacity << 1;
		this.baseHash = new int[baseHashSize];
		this.hashFactor = baseHashSize - 1;
		this.size = 0;
		clear();
	}

	private void privateAdd(int key) {
		int hashIndex = calcBaseHashIndex(key);
		int objectIndex = firstEmpty;
		firstEmpty = next[firstEmpty];
		keys[objectIndex] = key;
		next[objectIndex] = baseHash[hashIndex];
		baseHash[hashIndex] = objectIndex;
		++size;
	}

	protected int calcBaseHashIndex(int key) {
		return key & hashFactor;
	}

	public void clear() {
		Arrays.fill(this.baseHash, 0);
		size = 0;
		firstEmpty = 1;
		for (int i = 1; i < this.capacity;) {
			next[i] = ++i;
		}
		next[this.capacity] = 0;
	}

	public boolean contains(int value) {
		return find(value) != 0;
	}

	protected int find(int key) {
		int baseHashIndex = calcBaseHashIndex(key);
		int localIndex = baseHash[baseHashIndex];
		while (localIndex != 0) {
			if (keys[localIndex] == key) {
				return localIndex;
			}
			localIndex = next[localIndex];
		}
		return 0;
	}

	private int findForRemove(int key, int baseHashIndex) {
		this.prev = 0;
		int index = baseHash[baseHashIndex];
		while (index != 0) {
			if (keys[index] == key) {
				return index;
			}
			prev = index;
			index = next[index];
		}
		this.prev = 0;
		return 0;
	}

	protected void grow() {
		IntHashSet that = new IntHashSet(this.capacity * 2);
		for (IndexIterator iterator = new IndexIterator(); iterator.hasNext();) {
			int index = iterator.next();
			that.privateAdd(this.keys[index]);
		}
		this.capacity = that.capacity;
		this.size = that.size;
		this.firstEmpty = that.firstEmpty;
		this.keys = that.keys;
		this.next = that.next;
		this.baseHash = that.baseHash;
		this.hashFactor = that.hashFactor;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void printBaseHash() {
		for (int i = 0; i < this.baseHash.length; i++) {
			if (baseHash[i] != 0) {
				System.out.println(i + ".\t" + baseHash[i]);
			}
		}
	}

	public boolean add(int value) {
		int index = find(value);
		if (index != 0) {
			return true;
		}
		if (size == capacity) {
			grow();
		}
		privateAdd(value);
		return true;
	}

	public boolean remove(int value) {
		int baseHashIndex = calcBaseHashIndex(value);
		int index = findForRemove(value, baseHashIndex);
		if (index != 0) {
			if (prev == 0) {
				baseHash[baseHashIndex] = next[index];
			}
			next[prev] = next[index];
			next[index] = firstEmpty;
			firstEmpty = index;
			--size;
			return true;
		}

		return false;
	}

	public int size() {
		return this.size;
	}
}
