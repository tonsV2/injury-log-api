package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.User

interface UserService {
    fun save(user: User): User
    fun getByEmail(email: String): User
    fun findAll(): Iterable<User>
}
