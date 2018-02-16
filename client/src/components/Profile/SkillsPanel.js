import React, { Component } from 'react'

import '../../css/SkillsPanel.css'
import SkillsForm from './SkillsForm'

class SkillsPanel extends Component {
  render() {
    return (
      <div className="SkillsPanel">
        <SkillsForm />
      </div>
    )
  }
}

export default SkillsPanel