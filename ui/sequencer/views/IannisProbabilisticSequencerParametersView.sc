IannisProbabilisticSequencerParametersView : CompositeView {
	var playStopButton, resetButton, updateButton,
	patternLengthField, seedField, correspondingSequencer;

	*new {arg sequencer;
		^super.new.init(sequencer);
	}

	init {arg sequencer;
		var patternLengthLabel = StaticText.new;
		var seedLabel = StaticText.new;
		correspondingSequencer = sequencer;

		// pattern length
		patternLengthLabel.string = "Pattern length (beats):";
		patternLengthField = TextField.new;
		patternLengthField.fixedWidth = 50;

		patternLengthField.value = 8;

		patternLengthField.action = {arg field;
			this.lengthFieldAction(field);
		};

		patternLengthField.focusLostAction = {arg view;
			patternLengthField.doAction;
		};

		// seed
		seedLabel.string = "Seed:";
		seedField = TextField.new;
		seedField.fixedWidth = 100;
		seedField.value = sequencer.seed;

		seedField.action = {arg field;
			this.seedFieldAction(field);
		};

		seedField.focusLostAction = {arg view;
			seedField.doAction;
		};

		// play/stop button
		playStopButton = Button.new;
		playStopButton.states = [["Play"], ["Stop"]];
		playStopButton.fixedWidth = 200;

		playStopButton.action = {arg button;
			this.playStopButtonAction(button);
		};

		// reset button
		resetButton = Button.new;
		resetButton.states = [["Reset"]];
		resetButton.fixedWidth = 200;

		resetButton.action = {arg button;
			this.resetButtonAction(button);
		};

		// update button
		updateButton = Button.new;
		updateButton.states = [["Regenerate"]];
		updateButton.fixedWidth = 200;

		updateButton.action = {arg button;
			this.updateButtonAction(button);
		};

		this.layout = VLayout(
			// buttons
			HLayout(
				patternLengthLabel, patternLengthField,
				nil,
				seedLabel, seedField
			),

			HLayout(
				playStopButton, resetButton,
				nil,
				updateButton
			)
		);

		this.fixedHeight = 75;
	}

	lengthFieldAction {arg field;
		var length = field.value.asInt.clip(1, 999);
		patternLengthField.value = length;

		correspondingSequencer.changeLength(length);
	}

	seedFieldAction {arg field;
		var seed = field.value.asInt;
		field.value = seed;

		correspondingSequencer.setSeed(seed);
	}

	playStopButtonAction {arg button;
		if(button.value == 1, {
			correspondingSequencer.play();
		}, {
			correspondingSequencer.stop();
		});
	}

	resetButtonAction {arg button;
		correspondingSequencer.reset();
	}

	updateButtonAction {arg button;
		correspondingSequencer.regenerate();

		// update seed field
		seedField.value = correspondingSequencer.seed;
	}
}
