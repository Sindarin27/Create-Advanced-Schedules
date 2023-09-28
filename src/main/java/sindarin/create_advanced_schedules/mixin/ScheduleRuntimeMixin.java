package sindarin.create_advanced_schedules.mixin;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sindarin.create_advanced_schedules.AdvancedSchedules;
import sindarin.create_advanced_schedules.schedule.AdvanceToInstruction;
import sindarin.create_advanced_schedules.schedule.LabelInstruction;

import java.util.HashMap;
import java.util.List;

@Mixin(value = ScheduleRuntime.class, remap = false)
public abstract class ScheduleRuntimeMixin {
    @Shadow
    Schedule schedule;
    @Shadow
    public int currentEntry;
    @Shadow
    public ScheduleRuntime.State state;
    @Shadow
    Train train;
    @Final
    @Shadow
    static int INTERVAL;
    @Shadow
    int cooldown;
    @Shadow
    List<Integer> conditionProgress;
    @Shadow
    List<CompoundTag> conditionContext;

    @Unique
    private HashMap<String, Integer> create_advanced_schedules$labelEntries = new HashMap<>();

    @Inject(method = "startCurrentInstruction", at = @At(value = "HEAD"), cancellable = true)
    private void startCurrentInstruction(CallbackInfoReturnable<GlobalStation> cir) {
        ScheduleEntry entry = schedule.entries.get(currentEntry);
        ScheduleInstruction instruction = entry.instruction;

        if (instruction instanceof AdvanceToInstruction advance) {
            // First tick being called, set state such that condition will be checked
            state = ScheduleRuntime.State.POST_TRANSIT;

            List<List<ScheduleWaitCondition>> conditions = schedule.entries.get(currentEntry).conditions;
            for (int i = 0; i < conditions.size(); i++) {
                conditionProgress.add(0);
                conditionContext.add(new CompoundTag());
            }

            cir.setReturnValue(null);
        }

        // Label instruction. Simply move on to the next instruction
        if (instruction instanceof LabelInstruction) {
            state = ScheduleRuntime.State.PRE_TRANSIT;
            currentEntry++;
            cir.setReturnValue(null);
        } // Label does nothing
    }

    // TODO ensure this is also done when a schedule is loaded after a server restart
    @Inject(method = "setSchedule", at = @At(value = "TAIL"))
    private void storeScheduleLabels(Schedule schedule, boolean auto, CallbackInfo ci) {
        for (int i = 0; i < schedule.entries.size(); i++) {
            if (schedule.entries.get(i).instruction instanceof LabelInstruction label) {
                create_advanced_schedules$labelEntries.put(label.getLabel(), i);
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;tickConditions(Lnet/minecraft/world/level/Level;)V", remap = false))
    private void OnTickConditions(ScheduleRuntime instance, Level level) {
        if (schedule.entries.get(currentEntry).instruction instanceof AdvanceToInstruction advance) {
            boolean success = create_advanced_schedules$tickConditionsInstantFeedback(level);
            if (success) {

                // Success! Find matching label and jump to it.
                String targetLabel = advance.getTargetLabel();
                int targetEntry = create_advanced_schedules$labelEntries.getOrDefault(targetLabel, -1);

                if (targetEntry != -1) {
                    currentEntry = targetEntry;
                    state = ScheduleRuntime.State.PRE_TRANSIT;
                } else {
                    // TODO ensure this is only sent to the player once
                    // Can't find the correct entry! Shoot the player a message about this.
                    train.status.displayInformation("no_label", false);
                    cooldown = INTERVAL;
                }
            } else {
                // Failed condition, go to the next instruction
                state = ScheduleRuntime.State.PRE_TRANSIT;
                currentEntry++;
            }
        } else instance.tickConditions(level);

    }

    @Unique
    public boolean create_advanced_schedules$tickConditionsInstantFeedback(Level level) {
        // Update cargo
        for (Carriage carriage : train.carriages)
            carriage.storage.tickIdleCargoTracker();

//        AdvancedSchedules.LOGGER.debug("Getting conditions");
        List<List<ScheduleWaitCondition>> conditions = schedule.entries.get(currentEntry).conditions;
        for (int i = 0; i < conditions.size(); i++) {
//            AdvancedSchedules.LOGGER.debug("Getting condition context for " + i);
            if (conditionContext.size() <= i) {
                AdvancedSchedules.LOGGER.warn("Attempted to read non-existent condition context" + i + " when ticking " + schedule.entries.get(currentEntry).instruction.getId() + " (missing nbt?), created new compound tag");
                conditionContext.add(new CompoundTag());
            }
            CompoundTag tag = conditionContext.get(i);
//            AdvancedSchedules.LOGGER.debug("Getting list of conditions for " + i);
            List<ScheduleWaitCondition> list = conditions.get(i);

            boolean passedAll = true;

            for (ScheduleWaitCondition condition : list) {
                boolean completed = condition.tickCompletion(level, train, tag);
                if (completed) {
                    conditionContext.set(i, new CompoundTag());
                } else {
                    passedAll = false;
//                    AdvancedSchedules.LOGGER.debug("Found failed condition " + condition.getId());
                    break;
                }
            }
            if (passedAll) {
//                AdvancedSchedules.LOGGER.debug("All conditions passed");
                return true;
            }
        }
//        AdvancedSchedules.LOGGER.debug("All options failed");
        return false;
    }
}
