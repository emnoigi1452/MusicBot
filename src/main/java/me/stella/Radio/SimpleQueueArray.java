package me.stella.Radio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class SimpleQueueArray {
	
	private List<AudioTrack> queue;
	private AudioPlayer player;
	private int current;
	// 1 - no, 2 - current, 3 - list
	private byte loopMode;
	private Object[] jumpStatus;
	
	public SimpleQueueArray(AudioPlayer player) {
		this.player = player;
		this.queue = Collections.synchronizedList(new ArrayList<AudioTrack>());
		this.current = -1; this.loopMode = 0;
		this.jumpStatus = new Object[] { false, -1 };
	}
	
	public AudioPlayer _player() {
		return this.player;
	}
	
	public boolean _paused() {
		assert (this.player != null);
		return this.player.isPaused();
	}
	
	public void _pause() {
		assert (this.player != null);
		this.player.setPaused(true);
	}
	
	public int _now() {
		return this.current;
	}
	
	public boolean _jump(int i) {
		if(!((i > 0) && (i <= _list())))
			return false;
		this.jumpStatus = new Object[] { true, i };
		return true;
	}
	
	public int _list() {
		return this.queue.size();
	}
	
	public void _unpause() {
		assert (this.player != null);
		this.player.setPaused(false);
	}
	
	public synchronized List<String> pullTracks() {
		List<String> tracks = new LinkedList<String>();
		for(AudioTrack track: this.queue)
			tracks.add(AudioPanel.parseAudioName(track));
		return tracks;
	}
	
	public synchronized byte _loopMode(byte param) {
		try {
			assert (param >= 1) && (param <= 3);
			this.loopMode = param;
			return param;
		} catch(Throwable t) { return -1; }
	}
	
	public byte _loop() {
		return this.loopMode;
	}
	
	public synchronized TrackResponse _add(AudioTrack track) {
		try {
			assert (track != null);
			this.queue.add(track);
			return new TrackResponse(track, true);
		} catch(Throwable t) { return new TrackResponse(null, false); }
	}
	
	public AudioTrack _get() {
		try {
			assert (!(this.queue.isEmpty())) && (current >= 0);
			return this.queue.get(current);
		} catch(Throwable t) { return null; }
	}
	
	public synchronized TrackResponse _remove(int param) {
		assert (this.player != null);
		if(param == current) {
			this.player.stopTrack();
			current--;
		}
		try {
			assert (param < _list()) && (param >= 0);
			AudioTrack track = this.queue.remove(param);
			return new TrackResponse(track, true);
		} catch(Throwable t) { return new TrackResponse(null, false); }	
	}
	
	public AudioTrack _next() {
		if(Boolean.valueOf(String.valueOf(this.jumpStatus[0])).booleanValue()) {
			this.current = Integer.valueOf(String.valueOf(this.jumpStatus[1])) - 1;
			this.jumpStatus = new Object[] { false, -1 };
			return this.queue.get(current).makeClone();
		}
		if(this.queue.isEmpty())
			return null;
		if(loopMode == 2)
			return this.queue.get(current).makeClone();
		current++;
		if(current == _list()) {
			if(loopMode == 3) {
				current = 0;
				return this.queue.get(current).makeClone();
			}
			current--;
			return null;
		}
		return this.queue.get(current).makeClone();
	}
	
	public static class TrackResponse {
		private AudioTrack track;
		private boolean success;
		
		public TrackResponse(AudioTrack track, boolean success) {
			this.track = track;
			this.success = success;
		}
		
		public AudioTrack getTrack() {
			return this.track;
		}
		
		public boolean isSuccess() {
			return this.success;
		}
	}
	
}
