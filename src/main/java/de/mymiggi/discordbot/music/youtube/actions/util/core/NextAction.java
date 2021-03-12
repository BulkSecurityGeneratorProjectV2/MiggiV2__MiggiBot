package de.mymiggi.discordbot.music.youtube.actions.util.core;

import de.mymiggi.discordbot.music.youtube.actions.util.embed.PlayingNextEmbedAction;
import de.mymiggi.discordbot.music.youtube.actions.util.embed.UpdateQueueEmbedAction;
import de.mymiggi.discordbot.music.youtube.handler.InstantHandler;
import de.mymiggi.discordbot.music.youtube.util.AudioResource;
import de.mymiggi.discordbot.music.youtube.util.Queue;
import de.mymiggi.discordbot.music.youtube.util.Song;

public class NextAction
{
	public void run(int skipTo, boolean sendEmbed, Queue queue, AudioResource audioResource)
	{
		sendEmbed = false;
		for (int i = 0; i < skipTo; i++)
		{
			queue.currentTrackPositionAddOne();
		}
		if (queue.getSongs().size() <= queue.getCurrentTrackPosition())
		{
			if (queue.isLooping())
			{
				queue.setCurrentTrackPosition(queue.getLoopingPostion());
				playTrack(queue, audioResource);
				if (sendEmbed)
				{
					new PlayingNextEmbedAction().run(queue, audioResource);
				}
				new UpdateQueueEmbedAction().run(queue);
			}
			else
			{
				new StopAction().run(queue, audioResource);
			}
		}
		else
		{
			playTrack(queue, audioResource);
			if (sendEmbed)
			{
				new PlayingNextEmbedAction().run(queue, audioResource);
			}
			new UpdateQueueEmbedAction().run(queue);
		}
	}

	private void playTrack(Queue queue, AudioResource audioResource)
	{
		int currentPostion = queue.getCurrentTrackPosition();
		Song nextSong = queue.getSongs().get(currentPostion);
		if (nextSong.getAudioSource() == null)
		{
			InstantHandler instantHandler = new InstantHandler(queue, audioResource);
			instantHandler.setReplaceWithAudioSource(true);
			instantHandler.set(nextSong.getSearchQurry(), false);
			audioResource.getPlayerManager().loadItem(nextSong.getSearchQurry(), instantHandler);
		}
		else
		{
			queue.setCurrentTrack(nextSong.getAudioSource().makeClone());
			audioResource.getPlayer().startTrack(queue.getCurrentTrack(), false);
		}
	}
}
