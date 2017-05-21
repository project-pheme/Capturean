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
package atos.knowledgelab.capture.bean;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

public class VolumeResult {

	@XmlTransient
	String dc;
	
	@XmlTransient
	Date fInit;
	
	@XmlTransient
	Date fEnd;
	
	@XmlTransient
	int size;
	
	@XmlTransient
	long volume;
	
	@XmlTransient
	long  volumePositive;
	
	@XmlTransient
	long  volumeNegative;
	
	@XmlTransient
	long  volumeStress;
	
	@XmlTransient
	long  volumeNonStress;
	
	@XmlTransient
	long  volumeDangerousness;
	
	@XmlTransient
	long  volumeNonDangerousness;
	
	
	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public Date getfInit() {
		return fInit;
	}

	public void setfInit(Date fInit) {
		this.fInit = fInit;
	}

	public Date getfEnd() {
		return fEnd;
	}

	public void setfEnd(Date fEnd) {
		this.fEnd = fEnd;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getVolumePositive() {
		return volumePositive;
	}

	public void setVolumePositive(long volumePositive) {
		this.volumePositive = volumePositive;
	}

	public long getVolumeNegative() {
		return volumeNegative;
	}

	public void setVolumeNegative(long volumeNegative) {
		this.volumeNegative = volumeNegative;
	}

	public long getVolumeStress() {
		return volumeStress;
	}

	public void setVolumeStress(long volumeStress) {
		this.volumeStress = volumeStress;
	}

	public long getVolumeNonStress() {
		return volumeNonStress;
	}

	public void setVolumeNonStress(long volumeNonStress) {
		this.volumeNonStress = volumeNonStress;
	}

	public long getVolumeDangerousness() {
		return volumeDangerousness;
	}

	public void setVolumeDangerousness(long volumeDangerousness) {
		this.volumeDangerousness = volumeDangerousness;
	}

	public long getVolumeNonDangerousness() {
		return volumeNonDangerousness;
	}

	public void setVolumeNonDangerousness(long volumeNonDangerousness) {
		this.volumeNonDangerousness = volumeNonDangerousness;
	}
}
