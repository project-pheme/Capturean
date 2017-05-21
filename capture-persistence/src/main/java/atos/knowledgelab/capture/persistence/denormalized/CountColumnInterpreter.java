/*******************************************************************************
 * 	Copyright (C) 2017  ATOS Spain S.A.
 *
 * 	This file is part of the Capturean software.
 *
 * 	This program is dual licensed under the terms of GNU Affero General
 * 	Public License and proprietary for commercial usage.
 *
 *
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU Affero General Public License as
 * 	published by the Free Software Foundation, either version 3 of the
 * 	License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 *
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 	You can be released from the requirements of the license by purchasing
 * 	a commercial license or negotiating an agreement with Atos Spain S.A.
 * 	Buying such a license is mandatory as soon as you develop commercial
 * 	activities involving the Capturean software without disclosing the source 
 * 	code of your own applications. 
 *
 * 	
 * Contributors:
 *      Mateusz Radzimski (ATOS, ARI, Knowledge Lab)
 *      Iván Martínez Rodriguez (ATOS, ARI, Knowledge Lab)
 *      María Angeles Sanguino Gonzalez (ATOS, ARI, Knowledge Lab)
 *      Jose María Fuentes López (ATOS, ARI, Knowledge Lab)
 *      Jorge Montero Gómez (ATOS, ARI, Knowledge Lab)
 *      Ana Luiza Pontual Costa E Silva (ATOS, ARI, Knowledge Lab)
 *      Miguel Angel Tinte García (ATOS, ARI, Knowledge Lab)
 *      
 *******************************************************************************/
package atos.knowledgelab.capture.persistence.denormalized;

import java.io.IOException;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.coprocessor.ColumnInterpreter;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos.EmptyMsg;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos.LongMsg;

import com.google.protobuf.Message;


public class CountColumnInterpreter extends ColumnInterpreter<Object, Object, Message, Message, Message> {

	@Override
	public Object getValue(byte[] colFamily, byte[] colQualifier, Cell c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object add(Object l1, Object l2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getMaxValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getMinValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object multiply(Object o1, Object o2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object increment(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object castToReturnType(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compare(Object l1, Object l2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double divideForAvg(Object o, Long l) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Message getRequestData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message getProtoForCellType(Object t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCellValueFromProto(Message q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message getProtoForPromotedType(Object s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPromotedValueFromProto(Message r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object castToCellType(Object response) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
