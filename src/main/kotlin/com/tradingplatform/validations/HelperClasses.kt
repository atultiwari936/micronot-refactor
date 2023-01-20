import com.tradingplatform.model.Users

class UserValidation {
    val emailRegex="([a-zA-Z0-9]+([+._-]?[a-zA-z0-9])*)[@]([a-zA-Z]+[-]*[a-zA-z0-9]+[.])+[a-zA-Z]{2,}"
    val userNameRegex="^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]\$"
    val nameRegex="^[a-zA-z ]*\$"
    val phoneNumberRegex="^[0-9]{10}\$"
    fun isUserExists(list: ArrayList<String>,userName: String)
    {
        if(!Users.containsKey(userName))
            list.add("User Not Exist")
    }
    fun isEmailValid (list :ArrayList<String>,email:String)
    {
        if(!(email.isNotEmpty()&&emailRegex.toRegex().matches(email)))
        {
            list.add("Invalid Email format")
        }
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