/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.definitions.WorldMapDefinition;
import net.runelite.cache.definitions.WorldMapElementDefinition;
import net.runelite.cache.definitions.exporters.WorldMapDumpExporter;
import net.runelite.cache.definitions.loaders.WorldMapLoader;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class WorldMapDumperTest
{
	private static final Logger logger = LoggerFactory.getLogger(WorldMapDumperTest.class);
	private final List<WorldMapDefinition> worldMapDefinitions = new ArrayList<>();

	Store store;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public WorldMapDumperTest(Store store){
		this.store = store;
	}

	public void extract(File out) throws IOException
	{
			out.mkdirs();

		int count = 0;

		try
		{

			Storage storage = store.getStorage();
			Index index = store.getIndex(IndexType.WORLDMAP);
			Archive archive = index.findArchiveByName("details");

			byte[] archiveData = storage.loadArchive(archive);
			ArchiveFiles files = archive.getFiles(archiveData);

			for (FSFile file : files.getFiles())
			{
				WorldMapLoader loader = new WorldMapLoader();
				WorldMapDefinition def = loader.load(file.getContents(), file.getFileId());

				worldMapDefinitions.add(def);
				++count;
			}

			WorldMapDumpExporter exporter = new WorldMapDumpExporter(worldMapDefinitions);

			File targ = new File(out,"worldMapDump.json");
			exporter.exportTo(targ);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Dumped {} world map data to {}", count, out);
	}
}
