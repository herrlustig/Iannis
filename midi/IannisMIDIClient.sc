IannisMIDIClient : MIDIClient {
  classvar onUpdateMIDISources,
  sourcesWatcher, midiSourcesListSnaphot;

  *init {arg inports, outports, verbose = true;
    MIDIClient.init(inports, outports, verbose);
    MIDIIn.connectAll();

    midiSourcesListSnaphot = IannisMIDIClient.sources;
    
    onUpdateMIDISources.value();

    // sources watcher
    sourcesWatcher = SkipJack({
      IannisMIDIClient.list();

      if (IannisMIDIClient.sources.size > 0) {
        if ((midiSourcesListSnaphot.last.uid != IannisMIDIClient.sources.last.uid) || (midiSourcesListSnaphot.size != IannisMIDIClient.sources.size)) {
          midiSourcesListSnaphot = IannisMIDIClient.sources();
          MIDIClient.init();
          MIDIIn.connectAll();
          onUpdateMIDISources.value();
        };
      };
    }, 2);
  }

  *disposeClint {
    MIDIClient.disposeClint();
    sourcesWatcher.stop();
  }

  *restart {
    sourcesWatcher.stop();
    MIDIClient.restart();
    sourcesWatcher.start();
  }

  *addOnUpdateSourcesAction {arg func;
    onUpdateMIDISources = onUpdateMIDISources.addFunc(func);
  }

  *removeOnUpdateSourcesAction {arg func;
    onUpdateMIDISources = onUpdateMIDISources.removeFunc(func);
  }
}
