package atos.knowledgelab.capture.stream.serializers.impl;

import atos.knowledgelab.capture.stream.serializers.ISerialize;

public class RawSerialise implements ISerialize<String> {

	@Override
	public String serialize(String item) throws Exception {
		return item;
	}

}
