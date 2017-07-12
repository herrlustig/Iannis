IannisEffectsManager {
  classvar <availableEffects;
  var delegate,
  <effectsViewControllers,
  <group;

  *new {arg delegate;
    ^super.new.init(delegate);
  }

  init {arg aDelegate;
    delegate = aDelegate;
    group = Group();

    IannisEffectsManager.availableEffects??{
      IannisEffectsManager.fetchAvailableEffects();
    };

    effectsViewControllers = [];
  }

  cleanUp {
    effectsViewControllers.do({arg viewController;
      viewController.cleanUp();
    });

    group.free();
  }

  *fetchAvailableEffects {
    var synthDescs = SynthDescLib
    .getLib(\iannis_synth)
    .synthDescs
    .select({arg desc;
      desc.metadata[\type] == \effect;
    });

    availableEffects??{availableEffects = []};
    availableEffects = synthDescs;
  }

  addEffect {arg effectName;
    var newEffectView = IannisSynthViewController(effectName);

    newEffectView.node.moveToTail(this.group);

    newEffectView.front;

    effectsViewControllers = effectsViewControllers.add(newEffectView);

    // Inform the delegate
    delegate.didAddEffect();
  }

  removeEffectAtIndex {arg index;
    effectsViewControllers[index]!?{
      var removedViewController = effectsViewControllers.removeAt(index);

      delegate.willRemoveEffectAtIndex(index, removedViewController);

      removedViewController.close();
    }
  }

  changeEffectAtIndex {arg index, effectName;
    var newEffect = IannisSynthViewController(effectName);
    this.removeEffectAtIndex(index);

    effectsViewControllers[index-1]!?{
      newEffect.node.moveAfter(effectsViewControllers[index-1].node);
    }??{
      newEffect.node.moveToHead(this.group);
    };

    newEffect.front();

    effectsViewControllers = effectsViewControllers.insert(index, newEffect);

    delegate.didChangeEffectAtIndex(index, newEffect);
  }

  moveEffectToIndex {arg fromIndex, toIndex;
    if (fromIndex != toIndex) {
      var effectIndex = fromIndex.clip(0, effectsViewControllers.size-1);
      var targetIndex = toIndex.clip(0, effectsViewControllers.size-1);

      // restructure data
      effectsViewControllers.move(effectIndex, targetIndex);

      // move the node
      effectsViewControllers[targetIndex-1]!?{
        effectsViewControllers[targetIndex]
        .node
        .moveAfter(effectsViewControllers[targetIndex-1].node);
      }??{
        effectsViewControllers[targetIndex].node.moveToHead(this.group);
      };

      // inform the delegate
      delegate.didMoveEffectToIndex(fromIndex, toIndex);
    };
  }
}
