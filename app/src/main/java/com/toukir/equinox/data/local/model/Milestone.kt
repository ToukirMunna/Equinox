package com.toukir.equinox.data.local.model

data class MilestoneStage(
    val stageNumber: Int,
    val targetHours: Long,
    val title: String,
    val phaseSubtitle: String,
    val targetLabel: String,
    val neurobiology: String,
    val benefits: List<String>,
    val survivalTip: String
)

data class MilestoneProgress(
    val stage: MilestoneStage,
    val isUnlocked: Boolean,
    val isCurrentActive: Boolean,
    val progressPercent: Float,
    val hoursRemaining: Long
)

data class MilestoneJourneyState(
    val currentStage: MilestoneStage,
    val currentStageIndex: Int,
    val nextStage: MilestoneStage?,
    val overallProgressPercent: Float,
    val stageProgressPercent: Float,
    val hoursToNextMilestone: Long,
    val allStages: List<MilestoneProgress>
)

object MilestoneRepository {

    val STAGES: List<MilestoneStage> = listOf(
        MilestoneStage(
            stageNumber = 1,
            targetHours = 24,
            title = "The First Horizon",
            phaseSubtitle = "Acute Habit Interruption",
            targetLabel = "24 Hours (Day 1)",
            neurobiology = "Initial dopamine withdrawal spike begins. The prefrontal cortex actively exerts metabolic effort to override automatic subconscious cues and impulsive loops.",
            benefits = listOf(
                "Conscious willpower activation",
                "Breaking the autopilot loop",
                "Spike in self-respect & integrity"
            ),
            survivalTip = "Keep hands busy and phone far from the bed. The first 24 hours are won by changing physical environment immediately when a trigger strikes."
        ),
        MilestoneStage(
            stageNumber = 2,
            targetHours = 72,
            title = "The Crucible",
            phaseSubtitle = "Peak Chemical Withdrawal",
            targetLabel = "72 Hours (Day 3)",
            neurobiology = "Physical cravings and dopamine deficit reach their peak intensity. Dopamine D2 receptor sensitivity begins the first subtle upregulation as artificial super-stimuli are withheld.",
            benefits = listOf(
                "Spike in mental resilience",
                "Initial reduction in brain fog",
                "Enhanced alertness and focus"
            ),
            survivalTip = "Use ice-cold water on the face and Navy SEAL 4×4 Box Breathing whenever craving waves crest. Remember: cravings peak and naturally dissolve within 15 minutes."
        ),
        MilestoneStage(
            stageNumber = 3,
            targetHours = 168,
            title = "Clarity Awakening",
            phaseSubtitle = "Testosterone & Vitality Surge",
            targetLabel = "7 Days (1 Week)",
            neurobiology = "Clinical studies show serum testosterone levels surge up to 145% of baseline around day 7. Sleep architecture shifts into deeper REM restoration cycles.",
            benefits = listOf(
                "Noticeable boost in physical energy",
                "Deeper, more restorative sleep",
                "Sharper memory and eye contact",
                "Surge in natural motivation"
            ),
            survivalTip = "Channel this sudden physical energy into intense exercise, weight training, or focused creative work. Do not let excess energy sit idle."
        ),
        MilestoneStage(
            stageNumber = 4,
            targetHours = 336,
            title = "Foundation of Fortitude",
            phaseSubtitle = "Habit Loop Dissolution",
            targetLabel = "14 Days (2 Weeks)",
            neurobiology = "The acute withdrawal phase subsides. The ventral striatum is gradually recalibrating, reducing the emotional desperation associated with dopamine drops.",
            benefits = listOf(
                "Substantial decrease in craving frequency",
                "Clearer verbal fluency and mood stability",
                "Reduced social anxiety and shame",
                "Reclamation of lost daily hours"
            ),
            survivalTip = "Watch out for 'Flatline' (temporary emotional numbness) and 'Bargaining' (mind convincing you that you are cured). Stay vigilant."
        ),
        MilestoneStage(
            stageNumber = 5,
            targetHours = 720,
            title = "Neural Rewiring",
            phaseSubtitle = "Dopamine Baseline Normalization",
            targetLabel = "30 Days (1 Month)",
            neurobiology = "DeltaFosB accumulation in the reward pathway begins to degrade significantly. Everyday simple rewards (food, sunshine, meaningful conversations) feel emotionally vibrant again.",
            benefits = listOf(
                "Restored enjoyment of real life",
                "Dramatically increased discipline",
                "Solidified emotional stability",
                "True internal peace and presence"
            ),
            survivalTip = "Celebrate your 30-day victory with a healthy real-world reward (a good meal, a book, outdoor trip). Never celebrate recovery with poison."
        ),
        MilestoneStage(
            stageNumber = 6,
            targetHours = 1440,
            title = "Emotional Equilibrium",
            phaseSubtitle = "Prefrontal Solidification",
            targetLabel = "60 Days (2 Months)",
            neurobiology = "Gray matter density in the prefrontal cortex thickens. Top-down cognitive control is now biologically stronger than bottom-up limbic impulses.",
            benefits = listOf(
                "Unshakeable emotional resilience",
                "Zero panic response to sudden triggers",
                "Deep presence with family and partners",
                "Clear long-term goal execution"
            ),
            survivalTip = "Beware of complacency. Complacency is the silent killer of 60-day streaks. Maintain your morning routines and emergency checklist habits."
        ),
        MilestoneStage(
            stageNumber = 7,
            targetHours = 2160,
            title = "The Equinox",
            phaseSubtitle = "The 90-Day Full Neurobiological Reboot",
            targetLabel = "90 Days (3 Months)",
            neurobiology = "The gold standard 90-day neuroplastic reboot. Dopamine D2 receptor density has returned to baseline healthy levels. Compulsive neural pathways are substantially weakened.",
            benefits = listOf(
                "Complete neurochemical reset",
                "Profound inner confidence & presence",
                "Enhanced charisma and social magnetism",
                "Elimination of guilt, brain fog, and fatigue"
            ),
            survivalTip = "You have conquered the reboot. Now transition your mindset from 'avoiding relapse' to 'building an extraordinary life of purpose'."
        ),
        MilestoneStage(
            stageNumber = 8,
            targetHours = 4320,
            title = "Instinctive Mastery",
            phaseSubtitle = "Deep Neural Plasticity",
            targetLabel = "180 Days (6 Months)",
            neurobiology = "Long-Term Depression (LTD) has withered the old synaptic pathways of addiction. Self-mastery is no longer an active fight; it is your default operating state.",
            benefits = listOf(
                "Discipline operates automatically",
                "Profound mental clarity and focus",
                "High emotional bandwidth and empathy",
                "Unshakeable self-trust"
            ),
            survivalTip = "Mentor others starting on Day 1. Teaching and sharing your journey solidifies your own conviction."
        ),
        MilestoneStage(
            stageNumber = 9,
            targetHours = 8760,
            title = "Transformed Identity",
            phaseSubtitle = "Total Liberation & Life Reclamation",
            targetLabel = "365 Days (1 Year)",
            neurobiology = "Permanent epigenetic and synaptic transformation. You have forged a completely new baseline identity free from artificial dopamine traps.",
            benefits = listOf(
                "Complete freedom and sovereign peace",
                "A full year of compound self-growth",
                "Living as the highest version of yourself",
                "Unstoppable momentum in life and relationships"
            ),
            survivalTip = "Continue walking with humble gratitude. Mastery is a lifelong path of living in alignment with your deepest values."
        )
    )

    fun evaluate(streakStartTimestamp: Long): MilestoneJourneyState {
        val now = System.currentTimeMillis()
        val totalMillis = (now - streakStartTimestamp).coerceAtLeast(0L)
        val currentHours = totalMillis / (1000 * 3600.0)

        var currentActiveIndex = 0
        for (i in STAGES.indices) {
            if (currentHours < STAGES[i].targetHours) {
                currentActiveIndex = i
                break
            }
            if (i == STAGES.lastIndex) {
                currentActiveIndex = STAGES.lastIndex
            }
        }

        val currentStage = STAGES[currentActiveIndex]
        val prevTargetHours = if (currentActiveIndex == 0) 0L else STAGES[currentActiveIndex - 1].targetHours
        val nextTargetHours = currentStage.targetHours

        val stageSpan = (nextTargetHours - prevTargetHours).coerceAtLeast(1L)
        val hoursIntoStage = (currentHours - prevTargetHours).coerceAtLeast(0.0)
        val stageProgress = if (currentHours >= STAGES.last().targetHours) {
            1f
        } else {
            (hoursIntoStage / stageSpan.toDouble()).toFloat().coerceIn(0f, 1f)
        }

        val hoursRemaining = if (currentHours >= STAGES.last().targetHours) {
            0L
        } else {
            (nextTargetHours - currentHours).coerceAtLeast(0.0).toLong()
        }

        val overallProgress = (currentHours / STAGES.last().targetHours.toDouble()).toFloat().coerceIn(0f, 1f)

        val stageProgressList = STAGES.mapIndexed { index, stage ->
            val isUnlocked = currentHours >= stage.targetHours
            val isCurrent = index == currentActiveIndex && !isUnlocked
            val progress = when {
                isUnlocked -> 1f
                isCurrent -> stageProgress
                else -> 0f
            }
            val remaining = (stage.targetHours - currentHours).coerceAtLeast(0.0).toLong()
            MilestoneProgress(
                stage = stage,
                isUnlocked = isUnlocked,
                isCurrentActive = isCurrent,
                progressPercent = progress,
                hoursRemaining = remaining
            )
        }

        val nextStage = if (currentActiveIndex < STAGES.lastIndex) STAGES[currentActiveIndex] else null

        return MilestoneJourneyState(
            currentStage = currentStage,
            currentStageIndex = currentActiveIndex + 1,
            nextStage = nextStage,
            overallProgressPercent = overallProgress,
            stageProgressPercent = stageProgress,
            hoursToNextMilestone = hoursRemaining,
            allStages = stageProgressList
        )
    }
}
