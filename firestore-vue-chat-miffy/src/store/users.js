const state = {
  all: {},
  currentUser: 'miffy'
}

const mutations = {
  SET_USER (state, { user }) {
    state.all = {...state.all, [user.id]: user.data() }
  }
}

const actions = {
  seed ({ rootState }) {
    let userRef = rootState.db.collection('users')

    userRef.doc('miffy').set({
      firstName: 'Miffy',
      lastName: 'Chen'
    })

    userRef.doc('hugh').set({
      firstName: 'Hugh',
      lastName: 'Gao'
    })

    userRef.doc('mike').set({
      firstName: 'Michael',
      lastName: 'Hachey'
    })

    userRef.doc('neha').set({
      firstName: 'Neha',
      lastName: 'Pawar'
    })
  },

  async get ({ commit, rootState }) {
    let userRef = rootState.db.collection('users')
    let users = await userRef.get()

    users.forEach(user => commit('SET_USER', { user }))
  }
}

export default {
  namespaced: true, state, mutations, actions
}
