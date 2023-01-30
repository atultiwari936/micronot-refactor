import com.tradingplatform.model.Users
import io.micronaut.json.tree.JsonNode
import io.micronaut.json.tree.JsonObject
import java.util.Objects

class UserValidation {
    val emailRegex="([a-zA-Z0-9]+([+._-]?[a-zA-z0-9])*)[@]([a-zA-Z]+([-]?[a-zA-z0-9])+[.])+[a-zA-Z]{2,}"
    val userNameRegex="([a-zA-Z]+[(a-zA-z0-9)|_]*){3,}"
    val nameRegex="^[a-zA-z ]*\$"
    val phoneNumberRegex="^[+]+[0-9]{1,3}[0-9]{10}\$"
    fun isUserExists(list: ArrayList<String>,userName: String)
    {
        if(userName==null) {
            list.add("Username is Null")
            return
        }
        if(!Users.containsKey(userName))
            list.add("User Not Exist")
    }
    fun isEmailValid (list :ArrayList<String>,email:String)
    {
        var delimiter = "@"
        val parts = email.split(delimiter)
        if(!(email.isNotEmpty() && emailRegex.toRegex().matches(email)))
        {
            list.add("Invalid Email format")
        }
        else if(parts[0].length>64||parts[1].length>255)
        {
            list.add("max email length exceeded")
        }
        else if(!isEmailUnique(email))
        {
            list.add("Email Id already registered")
        }
    }

    private fun isEmailUnique(email: String):Boolean
    {
        for (user in Users.keys) {
            if(Users[user]!!.email==email)
            {
                return false
            }
        }
        return true
    }

    fun isPhoneValid (list :ArrayList<String>,phoneNumber:String): Boolean
    {
        if(!(phoneNumber.isNotEmpty() && phoneNumberRegex.toRegex().matches(phoneNumber)))
        {
            list.add("Invalid PhoneNumber format")
            return false
        }
        else if(!isPhoneUnique(phoneNumber))
        {
            list.add("Phone Number already registered")
            return false
        }
        return true
    }

    private fun isPhoneUnique(phoneNumber: String):Boolean
    {
        for (user in Users.keys) {
            if(Users[user]!!.phoneNumber==phoneNumber)
            {
                return false
            }
        }
        return true
    }



    fun isUserNameValid (list :ArrayList<String>,userName:String): Boolean
    {
        if(!(userName.isNotEmpty() && userNameRegex.toRegex().matches(userName)))
        {
            list.add("Invalid Username format")
            return false
        }
        else if(!isUnameUnique(userName))
        {
            list.add("Username already registered")
            return false
        }
        return true
    }

    private fun isUnameUnique(userName: String):Boolean
    {
        for (user in Users.keys) {
            if(Users[user]!!.userName==userName)
            {
                return false
            }
        }
        return true
    }


    fun isNameValid (list :ArrayList<String>,name:String) : Boolean
    {
        if(!(name.isNotEmpty()&& nameRegex.toRegex().matches(name)))
        {
            list.add("Invalid Name format")
            return false
        }
        return true
    }

    fun isFieldExists(fieldName:String, body: JsonObject ): Boolean
    {
        return body[fieldName] == null
    }

}


class OrderValidation {
    fun isValidAmount(list:ArrayList<String>,amount :Int, fieldName: String):Boolean
    {
        if(amount<=0)
        {
            list.add("Enter a positive $fieldName")
            return false
        }
        else if(amount>2147483640)
        {
            list.add("Enter $fieldName between 0 to 2147483640")
            return false
        }
        return true

    }

    fun isFieldExists(fieldName:String, body: JsonObject ): Boolean
    {
        return body[fieldName] == null
    }

    fun isValidEsopType(list:ArrayList<String>, esopType: String) : Boolean {
        if (esopType == "PERFORMANCE" || esopType == "NORMAL") {
            return true
        }

        list.add("ESOP type is not valid (Allowed : PERFORMANCE and NON-PERFORMANCE)")
        return false
    }

    fun isValidQuantity(list:ArrayList<String>,amount :Int)
    {
        if(amount<=0 || amount>2147483640)
        {
            list.add("Quantity is not valid. Minimum quantity is 1 and maximum is 2147483640")
        }
    }

    fun isValidOrderType(list:ArrayList<String>,type:String)
    {
       var array = arrayListOf<String>("PERFORMANCE")
        if(type !in array)
            list.add("Invalid Order type (Allowed : PERFORMANCE)")

    }
}

class DataTypeValidation{
    fun isDataTypeValid(list: ArrayList<String>,input:Any,reqType:String)
    {
        println(input::class.simpleName==reqType)
        if(input==null||input::class.simpleName==reqType)
            list.add("Invalid Order type")
    }
}
