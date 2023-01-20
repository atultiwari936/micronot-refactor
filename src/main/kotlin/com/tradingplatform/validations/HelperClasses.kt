import com.tradingplatform.model.Users
import io.micronaut.json.tree.JsonObject

class UserValidation {
    val emailRegex="([a-zA-Z0-9]+([+._-]?[a-zA-z0-9])*)[@]([a-zA-Z]+[-]*[a-zA-z0-9]+[.])+[a-zA-Z]{2,}"
    val userNameRegex="^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){0,20}\$"
    val nameRegex="^[a-zA-z ]*\$"
    val phoneNumberRegex="^[+]+(9)+(1)+[0-9]{10}\$"
    fun isUserExists(list: ArrayList<String>,userName: String)
    {
        if(!Users.containsKey(userName))
            list.add("User Not Exist")
    }
    fun isEmailValid (list :ArrayList<String>,email:String)
    {
        if(!(email.isNotEmpty() && emailRegex.toRegex().matches(email)))
        {
            list.add("Invalid Email format")
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

    fun isPhoneValid (list :ArrayList<String>,phoneNumber:String)
    {
        if(!(phoneNumber.isNotEmpty() && phoneNumberRegex.toRegex().matches(phoneNumber)))
        {
            list.add("Invalid PhoneNumber format")
        }
        else if(!isPhoneUnique(phoneNumber))
        {
            list.add("Phone Number already registered")

        }
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



    fun isUserNameValid (list :ArrayList<String>,userName:String)
    {
        if(!(userName.isNotEmpty() && userNameRegex.toRegex().matches(userName)))
        {
            list.add("Invalid Username format")
        }
        else if(!isUnameUnique(userName))
        {
            list.add("Username already registered")

        }
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


    fun isNameValid (list :ArrayList<String>,name:String)
    {
        if(!(name.isNotEmpty()&& nameRegex.toRegex().matches(name)))
        {
            list.add("Invalid Name format")
        }
    }




    fun isFieldExists(fieldName:String, body: JsonObject ): Boolean
    {
        return body[fieldName] == null
    }

}


class OrderValidation {
    fun isValidAmount(list:ArrayList<String>,amount :Int)
    {
        if(amount<=0 || amount>2147483640)
        {
            list.add("Enter a valid amount")
        }
    }
}