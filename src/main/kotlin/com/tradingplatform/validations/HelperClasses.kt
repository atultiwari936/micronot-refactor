import com.tradingplatform.model.Users
import io.micronaut.json.tree.JsonNode
import io.micronaut.json.tree.JsonObject
import java.util.Objects

class UserValidation {
    val emailRegex="([a-zA-Z0-9]+([+._-]?[a-zA-z0-9])*)[@]([a-zA-Z]+[-]*[a-zA-z0-9]+[.])+[a-zA-Z]{2,}"
    val userNameRegex="^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]\$"
    val nameRegex="^[a-zA-z ]*\$"
    val phoneNumberRegex="^[0-9]{10}\$"
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
            list.add("Enter a valid amount in range of Int")
        }
    }
    fun isValidQuantity(list:ArrayList<String>,amount :Int)
    {
        if(amount<=0 || amount>2147483640)
        {
            list.add("Enter a valid Quantity in range of Int")
        }
    }
    fun isValidOrderType(list:ArrayList<String>,type:String)
    {
       var array = arrayListOf<String>("PERFORMANCE")
        if(type !in array)
            list.add("Invalid Order type")

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
