package com.exp4j.Helpers

import java.util.HashMap
import java.util.Random

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectIntMap
import com.exp4j.Functions.ChanceFunction
import com.exp4j.Functions.MathUtilFunctions
import com.exp4j.Functions.RandomFunction
import com.exp4j.Operators.BooleanOperators
import com.exp4j.Operators.PercentageOperator
import com.lyeeedar.Statistic
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class EquationHelper
{
	companion object
	{
		fun setVariableNames(expB: ExpressionBuilder, variableMap: ObjectFloatMap<String>, prefix: String)
		{
			for (key in variableMap.keys())
			{
				expB.variable(prefix + key)
			}
		}

		fun setVariableValues(exp: Expression, variableMap: ObjectFloatMap<String>, prefix: String)
		{
			val valuesToBeSet = exp.variableNames

			for (key in variableMap.keys())
			{
				exp.setVariable(prefix + key, variableMap.get(key, 0f).toDouble())

				valuesToBeSet.remove(prefix + key)
			}

			for (variable in valuesToBeSet)
			{
				exp.setVariable(variable, 0.0)
			}
		}

		@JvmOverloads fun createEquationBuilder(eqn: String, ran: Random = MathUtils.random): ExpressionBuilder
		{
			val expB = ExpressionBuilder(eqn)
			BooleanOperators.applyOperators(expB)
			expB.operator(PercentageOperator.operator)
			expB.function(RandomFunction(ran))
			expB.function(ChanceFunction(ran))
			MathUtilFunctions.applyFunctions(expB)

			return expB
		}

		fun evaluate(eqn: String, variableMap: ObjectFloatMap<String> = ObjectFloatMap(), ran: Random = MathUtils.random): Float
		{
			try
			{
				return eqn.toFloat()
			}
			catch (ex: Exception)
			{
				val expB = createEquationBuilder(eqn, ran)
				setVariableNames(expB, variableMap, "")
				val exp = expB.build()

				if (exp == null)
				{
					return 0f
				}
				else
				{
					setVariableValues(exp, variableMap, "")
					val rawVal = exp.evaluate()

					return rawVal.toFloat()
				}
			}
		}
	}
}

fun String.evaluate(variableMap: ObjectFloatMap<String> = ObjectFloatMap(), ran: Random = MathUtils.random): Float = EquationHelper.evaluate(this, variableMap, ran)